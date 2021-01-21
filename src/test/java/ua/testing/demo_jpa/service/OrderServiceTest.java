package ua.testing.demo_jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ua.testing.demo_jpa.dto.OrderDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.ApartmentRepository;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.repository.OrderItemRepository;
import ua.testing.demo_jpa.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ApartmentTimetableRepository apartmentTimetableRepository;
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ApartmentService apartmentService;

    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2021, 1, 14, 13, 0);
    public static final Long TEST_USER_ID = 1L;
    private static Long orderId;

    @BeforeEach
    public void setup() {
        Long orderId = initializeTestOrder();
        OrderServiceTest.orderId = orderId;
        Long apartmentId = initializeTestApartment();
        Long scheduleId = initializeTestSchedule(apartmentId, 0);
        initializeTestOrderItem(orderId, apartmentId, scheduleId, 0);
    }

    @AfterEach
    public void clean() {
        orderService.deleteOrder(orderId);
    }

    private Long initializeTestOrder() {
        Order order = Order
                .builder()
                .orderStatus(OrderStatus.NEW)
                .orderDate(TEST_DATE)
                .user(User
                        .builder()
                        .id(TEST_USER_ID)
                        .build())
                .build();
        order = orderRepository.save(order);
        return order.getId();
    }

    private Long initializeTestApartment() {
        Apartment apartment = Apartment
                .builder()
                .bedsCount(1)
                .isAvailable(true)
                .type(RoomType.STANDARD)
                .price(BigDecimal.valueOf(3000))
                .build();
        apartment = apartmentRepository.save(apartment);
        return apartment.getId();
    }

    private Long initializeTestSchedule(Long apartmentId, int testDateOffset) {
        ApartmentTimetable schedule = ApartmentTimetable
                .builder()
                .apartment(Apartment.builder().id(apartmentId).build())
                .startsAt(TEST_DATE.plusDays(testDateOffset).plusHours(testDateOffset))
                .endsAt(TEST_DATE.plusDays(testDateOffset + 1))
                .status(RoomStatus.BOOKED)
                .build();
        schedule = apartmentTimetableRepository.save(schedule);
        return schedule.getId();
    }

    private Long initializeTestOrderItem(Long orderId, Long apartmentId, Long scheduleId, int testDateOffset) {
        OrderItem item = OrderItem
                .builder()
                .order(Order.builder().id(orderId).build())
                .apartment(Apartment.builder().id(apartmentId).build())
                .schedule(ApartmentTimetable.builder().id(scheduleId).build())
                .amount(1)
                .startsAt(TEST_DATE.plusDays(testDateOffset).plusHours(testDateOffset))
                .endsAt(TEST_DATE.plusDays(testDateOffset + 1))
                .price(BigDecimal.valueOf(3000))
                .build();
        item = orderItemRepository.save(item);
        return item.getId();
    }

    @Test
    public void shouldFindAllSchedules() {
        Page<Apartment> apartments = apartmentService.getAllApartments(PageRequest.of(0, 20));
        log.error("{}", apartments.getContent());
        for (Apartment a : apartments.getContent()) {
            log.error("{}", a.getSchedule());
        }
        assertTrue(apartments.getContent().size() != 0);
    }

    @Test
    public void shouldCreateAndDeleteOrder() {
        OrderItemDTO item = OrderItemDTO
                .builder()
                .amount(1)
                .apartmentId(1L)
                .price(BigDecimal.valueOf(3000))
                .build();
        List<OrderItemDTO> items = Lists.list(item);

        OrderDTO dto = OrderDTO
                .builder()
                .orderItems(items)
                .startsAt(TEST_DATE.minusDays(1))
                .endsAt(TEST_DATE)
                .userEmail("b@b.com")
                .build();
        Long newOrderId = orderService.createNewOrder(dto);

        //@TODO assertions

        orderService.deleteOrder(newOrderId);
    }
}