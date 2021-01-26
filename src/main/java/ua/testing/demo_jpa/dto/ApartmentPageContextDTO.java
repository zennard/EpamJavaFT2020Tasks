package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.Apartment;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApartmentPageContextDTO {
    List<Apartment> apartments;
    PageDTO page;
    DateDTO date;
}
