package ua.testing.demo_jpa.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class VacationDateDTO {
    private LocalDate startsAt;
    private LocalDate endsAt;
}
