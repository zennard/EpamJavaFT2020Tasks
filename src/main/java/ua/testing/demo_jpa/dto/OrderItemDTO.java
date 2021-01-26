package ua.testing.demo_jpa.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderItemDTO {
    private Long apartmentId;
    private Integer amount;
    private BigDecimal price;
}
