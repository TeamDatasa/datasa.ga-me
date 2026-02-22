package datasa.service;

import datasa.domain.dto.CommentCreateRequest;
import datasa.domain.dto.CommentResponse;
import datasa.domain.dto.CommentUpdateRequest;
import datasa.domain.dto.TripCommentedEvent;
import datasa.domain.entity.Comment;
import datasa.domain.entity.CommentLike;
import datasa.domain.entity.Trip;
import datasa.domain.entity.User;
import datasa.repository.CommentLikeRepository;
import datasa.repository.CommentRepository;
import datasa.repository.TripRepository;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	
	private static final Comment.Status DELETED = Comment.Status.DELETED;
	private final TripRepository tripRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final ApplicationEventPublisher eventPublisher;
	
	@Transactional(readOnly = true)
	public List<CommentResponse> listByTrip(Long tripId) {
		if (!tripRepository.existsById(tripId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found");
		}
		
		return commentRepository.findByTrip_TripIdAndStatusNotOrderByCreatedAtAsc(tripId, DELETED)
				.stream()
				.map(this::toResponse)
				.toList();
	}
	
	@Transactional
	public CommentResponse create(Long tripId, String email, CommentCreateRequest req) {
		Trip trip = tripRepository.findById(tripId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Comment parent = null;
		if (req.parentCommentId() != null) {
			parent = commentRepository
					.findByCommentIdAndTrip_TripIdAndStatusNot(req.parentCommentId(), tripId, Comment.Status.DELETED)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parent comment"));
		}
		
		Comment saved = commentRepository.save(new Comment(trip, user, parent, req.content()));
		
		Long actorUserId = user.getUserId();
		Long ownerUserId = trip.getHostUser().getUserId(); // 글 작성자(호스트)
		Long commentId = saved.getCommentId();
		
		// ✅ 이벤트는 항상 발행 (리스너에서 본인 제외 처리)
		eventPublisher.publishEvent(new TripCommentedEvent(tripId, commentId, actorUserId, ownerUserId));
		
		return toResponse(saved);
	}

	
	@Transactional
	public CommentResponse update(Long commentId, String email, CommentUpdateRequest req) {
		Comment comment = commentRepository.findByCommentIdAndStatusNot(commentId, DELETED)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
		
		User me = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		// 본인 댓글만 수정
		if (!comment.getUser().getUserId().equals(me.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
		}
		
		// ACTIVE만 수정 허용
		if (!comment.isActive()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Comment is not editable");
		}
		
		comment.updateContent(req.content());
		return toResponse(comment);
	}
	
	@Transactional
	public void delete(Long commentId, String email) {
		Comment comment = commentRepository.findByCommentIdAndStatusNot(commentId, DELETED)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
		
		User me = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		// 본인 댓글만 삭제
		if (!comment.getUser().getUserId().equals(me.getUserId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your comment");
		}
		
		// 멱등 처리
		if (comment.getStatus() == DELETED) return;
		
		// 대댓글이 있으면: 부모댓글만 삭제 처리(내용 치환) + 대댓글 유지
		boolean hasReplies = commentRepository.existsByParent_CommentIdAndStatusNot(commentId, DELETED);
		
		if (hasReplies) {
			comment.markDeletedKeepingReplies(); // status=DELETED + content="삭제된 댓글입니다."
			return;
		}
		
		// 대댓글이 없으면: 소프트 삭제
		comment.markDeletedKeepingReplies();
	}
	
	/**
	 * 좋아요 토글
	 * - 있으면 삭제
	 * - 없으면 생성
	 */
	@Transactional
	public long toggleLike(Long commentId, String email) {
		Comment comment = commentRepository.findByCommentIdAndStatusNot(commentId, DELETED)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Long userId = user.getUserId();
		
		commentLikeRepository.findByComment_CommentIdAndUser_UserId(commentId, userId)
				.ifPresentOrElse(
						commentLikeRepository::delete,
						() -> commentLikeRepository.save(new CommentLike(comment, user))
				);
		
		return commentLikeRepository.countByComment_CommentId(commentId);
	}

	private CommentResponse toResponse(Comment c) {
		long likeCount = commentLikeRepository.countByComment_CommentId(c.getCommentId());

		User u = c.getUser();
		boolean deletedUser = (u != null && u.getStatus() == User.Status.DELETED);

		Long userId = deletedUser ? null : u.getUserId();
		String userName = deletedUser ? "탈퇴한 사용자" : u.getName();

		boolean hostCardOpenable = false;
		if (!deletedUser && u != null) {
			hostCardOpenable =
					u.getRole() == User.Role.HOST
							&& Boolean.TRUE.equals(u.getHostCardPublic());
		}

		return new CommentResponse(
				c.getCommentId(),
				c.getTrip().getTripId(),
				userId,
				userName,
				c.getParent() == null ? null : c.getParent().getCommentId(),
				c.getContent(),
				c.getStatus().name(),
				likeCount,
				c.getCreatedAt(),
				c.getUpdatedAt(),
				hostCardOpenable
		);
	}

}
