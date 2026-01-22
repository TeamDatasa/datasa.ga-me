package datasa.repository;

import datasa.domain.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
	
	Optional<CommentLike> findByComment_CommentIdAndUser_UserId(Long commentId, Long userId);
	
	long countByComment_CommentId(Long commentId);
}
