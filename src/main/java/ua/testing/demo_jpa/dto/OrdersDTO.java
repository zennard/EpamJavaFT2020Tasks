package ua.testing.demo_jpa.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrdersDTO {
    private List<OrderDTO> orders;
}
