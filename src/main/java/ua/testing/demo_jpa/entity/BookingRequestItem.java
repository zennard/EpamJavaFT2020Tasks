package ua.testing.demo_jpa.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "booking_request_item")
public class BookingRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_request_id", nullable = false)
    @ToString.Exclude
    private BookingRequest bookingRequest;

    @Column(name = "beds_count", nullable = false)
    private Integer beds_count;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RoomType type;
}
