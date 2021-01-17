package ua.testing.demo_jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "booking_request")
public class BookingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "request_date", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime requestDate;

    @Column(name = "starts_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endsAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking_request")
    @ToString.Exclude
    private List<BookingRequestItem> requestItems;

}
