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
public class OrderCreationDTO {
    String userEmail;
    LocalDateTime orderDate;
    LocalDateTime startsAt;
    LocalDateTime endsAt;
    List<OrderItemDTO> orderItems;
}
