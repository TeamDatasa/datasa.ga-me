package datasa.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(
        name = "TRIP_LANGUAGE",
        indexes = {
                @Index(name = "idx_trip_language_trip", columnList = "trip_id"),
                @Index(name = "idx_trip_language_code", columnList = "language_code")
        }
)
@Getter
@NoArgsConstructor
public class TripLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_language_id")
    private Long tripLanguageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "language_code", nullable = false, length = 120)
    private String languageCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
