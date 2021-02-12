package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.*;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.exceptions.EmptyOrderException;
import ua.testing.demo_jpa.exceptions.UserNotFoundException;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;
import ua.testing.demo_jpa.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApartmentTimetableRepository apartmentTimetableRepository;
    private final UserRepository userRepository;

    private static final String NOT_FOUND_RECORD = "Illegal record, record with this date doesn't exist!";
    public static final String NOT_FOUND_ORDER = "Cannot delete non-existing order with id ";
    private static final String RECORD_ALREADY_EXISTS = "Illegal record, record with this date already exists!";

    @Transactional
    public Long createNewOrder(OrderCreationDTO orderDTO) {
        orderDTO.getOrderItems().stream()
                .filter(item -> recordExists(orderDTO.getStartsAt(), orderDTO.getEndsAt(), item.getApartmentId()))
                .findFirst()
                .ifPresent(item -> {
                    throw new IllegalArgumentException(RECORD_ALREADY_EXISTS);
                });

        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User with this credentials wasn't found!"));

        Order order = getOrder(orderDTO, user);

        List<OrderItem> orderItems = new ArrayList<>();
        List<ApartmentTimetable> schedule = new ArrayList<>();

        List<OrderItemDTO> orderItemsDTO = orderDTO.getOrderItems();
        for (OrderItemDTO o : orderItemsDTO) {
            Apartment apartment = new Apartment();
            apartment.setId(o.getApartmentId());

            ApartmentTimetable scheduleItem = getScheduleItem(orderDTO, apartment);
            schedule.add(scheduleItem);

            orderItems.add(
                    OrderItem.builder()
                            .order(order)
                            .startsAt(orderDTO.getStartsAt())
                            .endsAt(orderDTO.getEndsAt())
                            .apartment(apartment)
                            .schedule(scheduleItem)
                            .price(o.getPrice())
                            .amount(o.getAmount())
                            .build()
            );
        }

        return saveOrder(order, schedule, orderItems);
    }

    private Order getOrder(OrderCreationDTO orderDTO, User user) {
        return Order
                .builder()
                .orderDate(orderDTO.getOrderDate())
                .orderStatus(OrderStatus.NEW)
                .user(user)
                .build();
    }

    private ApartmentTimetable getScheduleItem(OrderCreationDTO orderDTO, Apartment apartment) {
        return ApartmentTimetable.builder()
                .apartment(apartment)
                .startsAt(orderDTO.getStartsAt())
                .endsAt(orderDTO.getEndsAt())
                .status(RoomStatus.BOOKED)
                .build();
    }

    @Transactional
    public Long saveOrder(Order order, List<ApartmentTimetable> schedule, List<OrderItem> items) {
        orderRepository.save(order);
        apartmentTimetableRepository.saveAll(schedule);
        orderItemRepository.saveAll(items);
        return order.getId();
    }

    public boolean recordExists(LocalDateTime startsAt, LocalDateTime endsAt, Long apartmentId) {
        List<ApartmentTimetable> schedule = apartmentTimetableRepository.findAllByApartmentIdAndDate(
                startsAt, endsAt, apartmentId);
        return !schedule.isEmpty();
    }

    @Transactional
    public void updateOrderStatus(UpdateOrderDTO newOrderDTO) {
        Order order = orderRepository.findById(newOrderDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER + newOrderDTO.getId()));
        order.setOrderStatus(newOrderDTO.getStatus());
        orderRepository.save(order);

        if (newOrderDTO.getStatus().equals(OrderStatus.PAID)) {
            //@TODO pay for order with money

            List<ApartmentTimetable> schedule = new ArrayList<>();
            orderItemRepository.findAllByOrderId(newOrderDTO.getId())
                    .forEach(item -> {
                        ApartmentTimetable timeslot = apartmentTimetableRepository
                                .findById(item.getApartment().getId())
                                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_RECORD));
                        timeslot.setStatus(RoomStatus.PAID);
                        schedule.add(timeslot);
                    });

            apartmentTimetableRepository.saveAll(schedule);
        }
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        orderItemRepository.findAllByOrderId(orderId)
                .forEach(item -> {
                    orderItemRepository.deleteById(item.getId());
                    apartmentTimetableRepository.deleteById(item.getSchedule().getId());
                });
        orderRepository.deleteById(orderId);
    }

    //@TODO do a method for all enum types of order
    public Page<OrderDTO> getAllNewOrders(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAllByOrderStatus(OrderStatus.NEW, pageable);

        List<OrderDTO> ordersDTO = ordersPage
                .stream()
                .map(this::getOrderDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(ordersDTO, ordersPage.getPageable(),
                ordersPage.getTotalElements());
    }

    public Page<UserOrderDTO> getAllUserOrders(Pageable pageable, Long userId) {
        Page<Order> ordersPage = orderRepository.findAllByUserId(userId, pageable);

        List<UserOrderDTO> ordersDTO = ordersPage
                .stream()
                .map(this::getUserOrderDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(ordersDTO, ordersPage.getPageable(),
                ordersPage.getTotalElements());
    }

    private OrderDTO getOrderDTO(Order o) {
        List<OrderItem> items = orderItemRepository.findAllByOrderId(o.getId());
        if (items.isEmpty()) {
            throw new EmptyOrderException("Cannot create order without order items!");
        }

        List<OrderItemDTO> itemsDTO = getOrderItemDTOS(items);

        return OrderDTO.builder()
                .id(o.getId())
                .userEmail(o.getUser().getEmail())
                .orderDate(o.getOrderDate())
                .startsAt(items.get(0).getStartsAt())
                .endsAt(items.get(0).getEndsAt())
                .orderItems(itemsDTO)
                .build();
    }

    private UserOrderDTO getUserOrderDTO(Order o) {
        List<OrderItem> items = orderItemRepository.findAllByOrderId(o.getId());
        if (items.isEmpty()) {
            throw new EmptyOrderException("Cannot create order without order items!");
        }

        List<OrderItemDTO> itemsDTO = getOrderItemDTOS(items);

        return UserOrderDTO.builder()
                .id(o.getId())
                .status(o.getOrderStatus())
                .orderDate(o.getOrderDate())
                .startsAt(items.get(0).getStartsAt())
                .endsAt(items.get(0).getEndsAt())
                .orderItems(itemsDTO)
                .build();
    }

    private List<OrderItemDTO> getOrderItemDTOS(List<OrderItem> items) {
        return items
                .stream()
                .map(item -> OrderItemDTO.builder()
                        .amount(item.getAmount())
                        .price(item.getPrice())
                        .apartmentId(item.getApartment().getId())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
