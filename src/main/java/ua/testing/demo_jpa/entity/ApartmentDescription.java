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
@Table(name = "apartment_description")
public class ApartmentDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    @ToString.Exclude
    private Apartment apartment;

    @Column(name = "description")
    private String description;

    @Column(name = "lang")
    @Enumerated(EnumType.STRING)
    private Language lang;
}
