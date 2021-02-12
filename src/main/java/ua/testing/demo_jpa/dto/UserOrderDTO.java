package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserOrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private OrderStatus status;
    private List<OrderItemDTO> orderItems;
}
