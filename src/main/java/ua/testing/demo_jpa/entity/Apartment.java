package ua.testing.demo_jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

@Entity
@Table(name = "apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "beds_count", nullable = false)
    private Integer bedsCount;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RoomType type;

    private String description;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apartment")
    @ToString.Exclude
    private List<ApartmentTimetable> schedule;
}
