package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookingRequestDTO {
    private Long id;
    private Long userId;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private LocalDateTime requestDate;
    private List<BookingRequestItemDTO> requestItems;
    private RequestStatus requestStatus;
}
