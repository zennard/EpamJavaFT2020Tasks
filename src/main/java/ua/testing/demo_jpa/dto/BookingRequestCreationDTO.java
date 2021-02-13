package ua.testing.demo_jpa.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookingRequestCreationDTO {
    private Long userId;
    private LocalDate startsAt;
    private LocalDate endsAt;
    private List<BookingRequestItemDTO> requestItems;
}
