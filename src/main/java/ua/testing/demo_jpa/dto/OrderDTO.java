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
public class OrderDTO {
    Long userId;
    LocalDateTime startsAt;
    LocalDateTime endsAt;
    List<OrderItemDTO> orderItems;
}