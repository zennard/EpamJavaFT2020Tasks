package ua.testing.demo_jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.testing.demo_jpa.dto.OrderDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void shouldGetAllNewOrders() {
        //given
        int expectedNumber = 1;
        int expectedOrderDtoId = 11;
        when(orderItemRepository.findAllByOrderId(any()))
                .thenReturn(givenOrderItems());
        when(orderRepository.findAllByOrderStatus(any(), any()))
                .thenReturn(givenOrders());
        //when
        Page<OrderDTO> orders = orderService.getAllNewOrders(mock(Pageable.class));
        //then
        assertThat(orders.getTotalElements()).isEqualTo(expectedNumber);

        OrderDTO orderDTO = orders.getContent().get(0);
        assertThat(orderDTO)
                .hasFieldOrPropertyWithValue("id", 11L);
    }

    private List<OrderItem> givenOrderItems() {
        return Collections.singletonList(
                OrderItem.builder()
                        .id(123L)
                        .apartment(mock(Apartment.class))
                        .build()
        );
    }

    private Page<Order> givenOrders() {
        return new PageImpl<>(Arrays.asList(
                Order.builder()
                        .id(11L)
                        .orderStatus(OrderStatus.NEW)
                        .user(mock(User.class))
                        .build()

        ));
    }
}