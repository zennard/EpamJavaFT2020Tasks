package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.OrderDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.exceptions.OrderDeletionException;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private ApartmentTimetableRepository apartmentTimetableRepository;

    private static final String NOT_FOUND_RECORD = "Illegal record, record with this date doesn't exist!";
    private static final String NOT_FOUND_ITEM = "Illegal order item, item with this id doesn't exist!";
    public static final String NOT_FOUND_ORDER = "Cannot delete non-existing order with id ";
    private static final String RECORD_ALREADY_EXISTS = "Illegal record, record with this date already exists!";

    @Transactional
    public void createNewOrder(OrderDTO orderDTO) {
        //@TODO check if some rooms are already booked by someone else
        User user = new User();
        user.setId(orderDTO.getUserId());
        Order order = Order
                .builder()
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.NEW)
                .user(user)
                .build();
        orderRepository.save(order);

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
            if (!recordExists(item.getStartsAt(), item.getEndsAt())) {
                throw new OrderDeletionException(NOT_FOUND_RECORD);
            }
            apartmentTimetableRepository.deleteById(item.getSchedule().getId());

            if (!orderItemRepository.findById(item.getId()).isPresent()) {
                throw new OrderDeletionException(NOT_FOUND_ITEM);
            }
            orderItemRepository.deleteById(item.getId());
        }
        orderItemRepository.deleteById(orderId);
    }

    public boolean recordExists(LocalDateTime startsAt, LocalDateTime endsAt) {
        List<ApartmentTimetable> schedule = apartmentTimetableRepository.findAllByStartsAtGreaterThanEqualAndEndsAtLessThanEqual(
                startsAt, endsAt);
        return !schedule.isEmpty();
    }
}
