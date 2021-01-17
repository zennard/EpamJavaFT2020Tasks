package ua.testing.demo_jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "apartment_timetable")
public class ApartmentTimetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    @ToString.Exclude
    private Apartment apartment;

    @Column(name = "starts_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endsAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
}
