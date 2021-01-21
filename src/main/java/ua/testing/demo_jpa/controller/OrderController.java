package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.testing.demo_jpa.dto.OrderDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.service.ApartmentService;
import ua.testing.demo_jpa.service.OrderService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ApartmentService apartmentService;

    @Autowired
    public OrderController(OrderService orderService, ApartmentService apartmentService) {
        this.orderService = orderService;
        this.apartmentService = apartmentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postOrder(@RequestParam List<Long> apartmentIds,
                            @RequestParam(value = "startsAt", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startsAt,
                            @RequestParam(value = "endsAt", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endsAt,
                            Principal principal) {
        log.error("{}", apartmentIds);
        List<OrderItemDTO> items = apartmentService.getAllApartmentsByIds(apartmentIds);
        log.error("{}", items);

        OrderDTO orderDTO = OrderDTO
                .builder()
                .userEmail(principal.getName())
                .startsAt(startsAt)
                .endsAt(endsAt)
                .orderItems(items)
                .build();
        log.error("{}", orderDTO);
        orderService.createNewOrder(orderDTO);
        return String.format("redirect:/apartments?startsAt=%s&endsAt=%s", startsAt, endsAt);
    }

    @GetMapping
    public String getOrdersPage() {
        return "";
    }
}


