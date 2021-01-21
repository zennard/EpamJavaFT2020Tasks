package ua.testing.demo_jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.OrderDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.exceptions.OrderDeletionException;
import ua.testing.demo_jpa.exceptions.UserNotFoundException;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;
import ua.testing.demo_jpa.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApartmentTimetableRepository apartmentTimetableRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ApartmentTimetableRepository apartmentTimetableRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.apartmentTimetableRepository = apartmentTimetableRepository;
        this.userRepository = userRepository;
    }

    private static final String NOT_FOUND_RECORD = "Illegal record, record with this date doesn't exist!";
    private static final String NOT_FOUND_ITEM = "Illegal order item, item with this id doesn't exist!";
    public static final String NOT_FOUND_ORDER = "Cannot delete non-existing order with id ";
    private static final String RECORD_ALREADY_EXISTS = "Illegal record, record with this date already exists!";

    @Transactional
    public Long createNewOrder(OrderDTO orderDTO) {
        orderDTO.getOrderItems().forEach(item -> {
                    if (recordExists(
                            orderDTO.getStartsAt(), orderDTO.getEndsAt(), item.getApartmentId())) {
                        throw new IllegalArgumentException(RECORD_ALREADY_EXISTS);
                    }
                }
        );
        User user = userRepository.findByEmail(orderDTO.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User with this credentials wasn't found!"));

        Order order = Order
                .builder()
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.NEW)
                .user(user)
                .build();
        Order newOrder = orderRepository.save(order);

        List<OrderItemDTO> orderItems = orderDTO.getOrderItems();
        for (OrderItemDTO o : orderItems) {
            Apartment apartment = new Apartment();
            apartment.setId(o.getApartmentId());

            ApartmentTimetable schedule = apartmentTimetableRepository.save(ApartmentTimetable
                    .builder()
                    .apartment(apartment)
                    .startsAt(orderDTO.getStartsAt())
                    .endsAt(orderDTO.getEndsAt())
                    .status(RoomStatus.BOOKED)
                    .build());

            orderItemRepository.save(OrderItem
                    .builder()
                    .order(order)
                    .startsAt(orderDTO.getStartsAt())
                    .endsAt(orderDTO.getEndsAt())
                    .apartment(apartment)
                    .schedule(schedule)
                    .price(o.getPrice())
                    .amount(o.getAmount())
                    .build());
        }

        return newOrder.getId();
    }

    public void approveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER + orderId));
        order.setOrderStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
    }

    @Transactional
    public void payForOrder(Long orderId) {
        //@TODO pay for order with money

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_ORDER + orderId));
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        for (OrderItem item : orderItems) {
            ApartmentTimetable schedule = apartmentTimetableRepository.findById(item.getApartment().getId())
                    .orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_RECORD));
            schedule.setStatus(RoomStatus.PAID);
            apartmentTimetableRepository.save(schedule);
        }
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.findById(orderId).isPresent()) {
            throw new OrderDeletionException(NOT_FOUND_ORDER + orderId);
        }

        for (OrderItem item : orderItemRepository.findAllByOrderId(orderId)) {
            if (!orderItemRepository.findById(item.getId()).isPresent()) {
                throw new OrderDeletionException(NOT_FOUND_ITEM);
            }
            orderItemRepository.deleteById(item.getId());

            if (!recordExists(item.getStartsAt(), item.getEndsAt(), item.getApartment().getId())) {
                throw new OrderDeletionException(NOT_FOUND_RECORD);
            }
            apartmentTimetableRepository.deleteById(item.getSchedule().getId());
        }
        orderRepository.deleteById(orderId);
    }

    public boolean recordExists(LocalDateTime startsAt, LocalDateTime endsAt, Long apartmentId) {
        List<ApartmentTimetable> schedule = apartmentTimetableRepository.findAllByApartmentIdAndDate(
                startsAt, endsAt, apartmentId);
        return !schedule.isEmpty();
    }

    //@TODO
    public List<OrderDTO> getAllNewOrders(Pageable pageable) {
        return null;
    }
}
