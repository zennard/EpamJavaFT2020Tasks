package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.RequestStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookingRequestUpdateDTO {
    private Long id;
    private RequestStatus status;
}
