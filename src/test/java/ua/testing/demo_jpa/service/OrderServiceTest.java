package ua.testing.demo_jpa.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ua.testing.demo_jpa.dto.*;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;
import ua.testing.demo_jpa.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ApartmentTimetableRepository apartmentTimetableRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService testedInstance;

    @Test
    void shouldGetAllUserOrders() {
        //given
        when(orderRepository.findAllByUserId(anyLong(), any()))
                .thenReturn(givenUserOrders());
        when(orderItemRepository.findAllByOrderId(any()))
                .thenReturn(Collections.singletonList(givenOrderItems().get(0)),
                        Collections.singletonList(givenOrderItems().get(1)));

        //when
        Page<UserOrderDTO> orders = testedInstance.getAllUserOrders(null, 1L);
        //then
        AssertionsForClassTypes.assertThat(orders.getContent().get(1))
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("startsAt", LocalDateTime.of(2020, 10, 3, 10, 20))
                .hasFieldOrPropertyWithValue("endsAt", LocalDateTime.of(2020, 10, 4, 10, 20))
                .hasFieldOrPropertyWithValue("status", OrderStatus.APPROVED)
                .hasFieldOrPropertyWithValue("orderDate", LocalDateTime.of(2001, 2, 1, 10, 10));

        AssertionsForClassTypes.assertThat(orders.getContent().get(1).getOrderItems().get(0))
                .hasFieldOrPropertyWithValue("apartmentId", 2L);
    }

    private List<OrderItem> givenOrderItems() {
        return Arrays.asList(
                OrderItem.builder()
                        .id(1L)
                        .startsAt(LocalDateTime.of(2020, 10, 1, 10, 20))
                        .endsAt(LocalDateTime.of(2020, 10, 2, 10, 20))
                        .price(BigDecimal.valueOf(1000))
                        .order(Order.builder().id(1L).build())
                        .schedule(ApartmentTimetable.builder().id(1L).build())
                        .apartment(Apartment.builder().id(1L).build())
                        .build(),
                OrderItem.builder()
                        .id(2L)
                        .startsAt(LocalDateTime.of(2020, 10, 3, 10, 20))
                        .endsAt(LocalDateTime.of(2020, 10, 4, 10, 20))
                        .price(BigDecimal.valueOf(2000))
                        .order(Order.builder().id(2L).build())
                        .schedule(ApartmentTimetable.builder().id(2L).build())
                        .apartment(Apartment.builder().id(2L).build())
                        .build());
    }

    private Page<Order> givenUserOrders() {
        return new PageImpl<>(Arrays.asList(
                Order.builder()
                        .id(1L)
                        .user(User.builder().id(1L).build())
                        .orderStatus(OrderStatus.NEW)
                        .orderDate(LocalDateTime.of(2000, 2, 1, 10, 10))
                        .build(),
                Order.builder()
                        .id(2L)
                        .user(User.builder().id(1L).build())
                        .orderStatus(OrderStatus.APPROVED)
                        .orderDate(LocalDateTime.of(2001, 2, 1, 10, 10))
                        .build()
        ),
                PageRequest.of(0, 2), 1);
    }

    @Test
    void shouldGetAllNewOrders() {
        //given
        when(orderRepository.findAllByOrderStatus(any(), any()))
                .thenReturn(givenUserOrders());
        when(orderItemRepository.findAllByOrderId(anyLong()))
                .thenReturn(Collections.singletonList(givenOrderItems().get(0)),
                        Collections.singletonList(givenOrderItems().get(1)));

        //when
        Page<OrderDTO> orders = testedInstance.getAllNewOrders(null);
        //then
        assertThat(orders.getContent().get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("startsAt", LocalDateTime.of(2020, 10, 1, 10, 20))
                .hasFieldOrPropertyWithValue("endsAt", LocalDateTime.of(2020, 10, 2, 10, 20))
                .hasFieldOrPropertyWithValue("orderDate", LocalDateTime.of(2000, 2, 1, 10, 10));

        assertThat(orders.getContent().get(0).getOrderItems().get(0))
                .hasFieldOrPropertyWithValue("apartmentId", 1L);
    }

    @Test
    void shouldCreateNewOrder() {
        //given
        when(apartmentTimetableRepository.findAllByApartmentIdAndDate(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(User.builder().id(1L).email("a@a.com").build()));
        when(orderRepository.save(any()))
                .thenReturn(Order.builder().id(1L).build());

        //when
        Long newOrderId = testedInstance.createNewOrder(givenOrder());
        //then
        verify(orderRepository, times(1)).save(any());
    }

    private OrderCreationDTO givenOrder() {
        return OrderCreationDTO.builder()
                .orderDate(LocalDateTime.of(2020, 10, 1, 1, 1))
                .startsAt(LocalDateTime.of(2020, 11, 2, 10, 10))
                .endsAt(LocalDateTime.of(2020, 11, 3, 10, 10))
                .userEmail("a@a.com")
                .orderItems(givenOrderItemsDTO())
                .build();
    }

    private List<OrderItemDTO> givenOrderItemsDTO() {
        return Arrays.asList(
                OrderItemDTO.builder()
                        .price(BigDecimal.valueOf(3000))
                        .apartmentId(1L)
                        .build(),
                OrderItemDTO.builder()
                        .price(BigDecimal.valueOf(2000))
                        .apartmentId(2L)
                        .build()
        );
    }

    @Test
    void shouldUpdateOrderStatusCallOrderDaoUpdate() {
        //given
        when(orderRepository.findById(any()))
                .thenReturn(givenOrderOptional());

        //when
        testedInstance.updateOrderStatus(UpdateOrderDTO.builder()
                .id(1L)
                .status(OrderStatus.PAID)
                .build());
        //then
        verify(orderRepository, times(1)).save(any());
    }

    private Optional<Order> givenOrderOptional() {
        return Optional.of(
                Order.builder()
                        .id(1L)
                        .orderDate(LocalDateTime.of(2020, 5, 1, 10, 10))
                        .user(User.builder().id(1L).build())
                        .orderStatus(OrderStatus.NEW)
                        .build()
        );
    }
}