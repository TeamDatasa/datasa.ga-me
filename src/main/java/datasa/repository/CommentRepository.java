package datasa.repository;

import datasa.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	// trip 기준 댓글 목록 (삭제 제외)
	List<Comment> findByTrip_TripIdAndStatusNotOrderByCreatedAtAsc(Long tripId, Comment.Status status);
	
	// 댓글 단건 (삭제 제외)
	Optional<Comment> findByCommentIdAndStatusNot(Long commentId, Comment.Status status);
	
	// 부모댓글이 특정 trip에 속하는지 검증용
	Optional<Comment> findByCommentIdAndTrip_TripIdAndStatusNot(Long commentId, Long tripId, Comment.Status status);
	
	// 특정 댓글에 삭제되지 않은 대댓글이 존재하는지 검증
	boolean existsByParent_CommentIdAndStatusNot(Long parentCommentId, Comment.Status status);
	
}
