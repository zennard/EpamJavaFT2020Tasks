package ua.testing.demo_jpa.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderDTO {
    private Long id;
    private String userEmail;
    private LocalDateTime orderDate;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalPrice;
}
