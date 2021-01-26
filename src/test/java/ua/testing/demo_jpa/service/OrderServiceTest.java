package ua.testing.demo_jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ua.testing.demo_jpa.dto.OrderCreationDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@SpringBootTest
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
    private UserRepository userRepository;
    @Autowired
    private ApartmentDescriptionRepository apartmentDescriptionRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ApartmentService apartmentService;

    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2021, 1, 14, 13, 0);
    public static final int TEST_DATE_OFFSET = 4;
    public static final Long TEST_USER_ID = 1L;
    private static final Long ORDER_ID = 1L;
    private static final Long APARTMENT_ID = 1L;
    private static final Long SCHEDULE_ID = 1L;
    public static final Long ORDER_ITEM_ID = 1L;
    public static final Pageable PAGEABLE = PageRequest.of(0, 20);

    @Test
    void functionTest() {
        User user = new User();
        user.setEmail("email@mail.com");
        userRepository.save(user);
        List<User> users = (List<User>) userRepository.findAll();
        assertEquals(users.size(), 1);
    }

//    @BeforeEach
//    public void setUp() {
//        Order order = initializeTestOrder(ORDER_ID);
//        Apartment apartment = initializeTestApartment(APARTMENT_ID);
//        ApartmentTimetable schedule = initializeTestSchedule(SCHEDULE_ID, APARTMENT_ID, TEST_DATE_OFFSET);
//        OrderItem orderItem = initializeTestOrderItem(ORDER_ITEM_ID, ORDER_ID, APARTMENT_ID, SCHEDULE_ID, TEST_DATE_OFFSET);
//
//        Mockito.when(orderRepository.findAllByOrderStatus(order.getOrderStatus(), PAGEABLE))
//                .thenReturn(new PageImpl<>(Collections.singletonList(order)));
//
//    }

//    @BeforeEach
//    public void setup() {
//        Long orderId = initializeTestOrder();
//        OrderServiceTest.orderId = orderId;
//        Long apartmentId = initializeTestApartment();
//        Long scheduleId = initializeTestSchedule(apartmentId, 0);
//        initializeTestOrderItem(orderId, apartmentId, scheduleId, 0);
//    }

    @AfterEach
    public void clean() {
        orderService.deleteOrder(ORDER_ID);
    }

    private Order initializeTestOrder(Long newOrderId) {
        return Order
                .builder()
                .id(newOrderId)
                .orderStatus(OrderStatus.NEW)
                .orderDate(TEST_DATE)
                .user(User
                        .builder()
                        .id(TEST_USER_ID)
                        .build())
                .build();
    }

    private Apartment initializeTestApartment(Long apartmentId) {
        return Apartment
                .builder()
                .id(apartmentId)
                .bedsCount(1)
                .isAvailable(true)
                .type(RoomType.STANDARD)
                .price(BigDecimal.valueOf(3000))
                .build();
    }

    private ApartmentTimetable initializeTestSchedule(Long scheduleId, Long apartmentId, int testDateOffset) {
        return ApartmentTimetable
                .builder()
                .id(scheduleId)
                .apartment(Apartment.builder().id(apartmentId).build())
                .startsAt(TEST_DATE.plusDays(testDateOffset).plusHours(testDateOffset))
                .endsAt(TEST_DATE.plusDays(testDateOffset + 1))
                .status(RoomStatus.BOOKED)
                .build();
    }

    private OrderItem initializeTestOrderItem(Long orderItemId, Long orderId, Long apartmentId, Long scheduleId, int testDateOffset) {
        return OrderItem
                .builder()
                .id(orderItemId)
                .order(Order.builder().id(orderId).build())
                .apartment(Apartment.builder().id(apartmentId).build())
                .schedule(ApartmentTimetable.builder().id(scheduleId).build())
                .amount(1)
                .startsAt(TEST_DATE.plusDays(testDateOffset).plusHours(testDateOffset))
                .endsAt(TEST_DATE.plusDays(testDateOffset + 1))
                .price(BigDecimal.valueOf(3000))
                .build();
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

        OrderCreationDTO dto = OrderCreationDTO
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