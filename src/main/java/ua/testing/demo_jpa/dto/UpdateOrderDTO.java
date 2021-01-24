package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UpdateOrderDTO {
    Long id;
    OrderStatus status;
}
