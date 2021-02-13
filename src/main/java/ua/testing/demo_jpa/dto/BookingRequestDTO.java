package ua.testing.demo_jpa.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookingRequestDTO {
    private Long userId;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private List<BookingRequestItemDTO> requestItems;
}
