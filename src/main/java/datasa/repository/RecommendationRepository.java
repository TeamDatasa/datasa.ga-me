package datasa.repository;

import datasa.domain.entity.RecommendationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendationRepository
        extends JpaRepository<RecommendationResult, Long> {

    @Query(value = """
        SELECT r.trip_id, r.score
        FROM recommendation_result r
        WHERE r.user_id = :userId
        ORDER BY r.score DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findAiRecommendations(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
}
