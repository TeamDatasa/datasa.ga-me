package datasa.repository;

import datasa.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByTrip_TripIdAndStatusNotOrderByCreatedAtAsc(Long tripId, Comment.Status status);

	Optional<Comment> findByCommentIdAndStatusNot(Long commentId, Comment.Status status);

	Optional<Comment> findByCommentIdAndTrip_TripIdAndStatusNot(Long commentId, Long tripId, Comment.Status status);

	boolean existsByParent_CommentIdAndStatusNot(Long parentCommentId, Comment.Status status);

	@Modifying
	@Query("delete from Comment c where c.trip.tripId = :tripId")
	void deleteByTripId(@Param("tripId") Long tripId);
}
