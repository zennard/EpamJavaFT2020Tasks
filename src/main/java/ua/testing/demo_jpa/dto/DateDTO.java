package ua.testing.demo_jpa.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DateDTO {
    Integer prevYear;
    Integer nextYear;
    LocalDate checkIn;
    LocalDate checkOut;
}
