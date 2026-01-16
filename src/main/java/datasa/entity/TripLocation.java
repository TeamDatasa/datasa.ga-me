package datasa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "trip_location",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_trip_location_order",
                        columnNames = {"trip_id", "order_no"}
                )
        }
)
@Getter
@NoArgsConstructor
public class TripLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_location_id")
    private Long tripLocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "order_no", nullable = false)
    private Integer orderNo;

    @Column(name = "memo", length = 255)
    private String memo;
}
