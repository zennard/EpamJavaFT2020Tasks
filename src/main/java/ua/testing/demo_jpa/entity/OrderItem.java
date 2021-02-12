package ua.testing.demo_jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_timetable_id", nullable = false)
    private ApartmentTimetable schedule;

    @Column(name = "starts_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endsAt;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "amount", nullable = false)
    private Integer amount;
}
