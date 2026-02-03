package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recommendation_result",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recommendation_user_trip",
                        columnNames = {"user_id", "trip_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecommendationResult {

    // =========================
    // PK
    // =========================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    // =========================
    // FK: User
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =========================
    // FK: Trip
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // =========================
    // AI 추천 점수
    // =========================
    @Column(name = "score", nullable = false)
    private Double score;

    // =========================
    // 어떤 모델로 생성됐는지
    // v1_rule / v2_lightfm
    // =========================
    @Column(name = "model_type", nullable = false, length = 20)
    private String modelType;

    //
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    //
    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }
    }
