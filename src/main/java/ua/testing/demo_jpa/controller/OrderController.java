package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.testing.demo_jpa.dto.*;
import ua.testing.demo_jpa.entity.OrderStatus;
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
    public static final String ORDERS_PAGE = "order_controller/orders.html";
    public static final String ORDERS_REDIRECT = "redirect:/orders/";

    @Autowired
    public OrderController(OrderService orderService, ApartmentService apartmentService) {
        this.orderService = orderService;
        this.apartmentService = apartmentService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String postOrder(@RequestParam List<Long> apartmentIds,
                            @RequestParam(value = "startsAt", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startsAt,
                            @RequestParam(value = "endsAt", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endsAt,
                            Principal principal) {
        log.info("{}", apartmentIds);
        List<OrderItemDTO> items = apartmentService.getAllApartmentsByIds(apartmentIds);
        log.info("{}", items);

        OrderCreationDTO orderDTO = OrderCreationDTO
                .builder()
                .userEmail(principal.getName())
                .orderDate(LocalDateTime.now())
                .startsAt(startsAt)
                .endsAt(endsAt)
                .orderItems(items)
                .build();

        log.info("{}", orderDTO);

        orderService.createNewOrder(orderDTO);
        
        return String.format("redirect:/apartments?startsAt=%s&endsAt=%s",
                startsAt.toLocalDate(), endsAt.toLocalDate());
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String getOrdersPage(Model model,
                                @PageableDefault(sort = {"id"}, size = 2) Pageable pageable) {
        Page<OrderDTO> orderPage = orderService.getAllNewOrders(pageable);
        Pageable currentPageable = orderPage.getPageable();

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("page", PageDTO
                .builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(orderPage.getTotalPages())
                .hasPrev(orderPage.hasPrevious())
                .hasNext(orderPage.hasNext())
                .url("/orders/")
                .build());

        return ORDERS_PAGE;
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String updateOrder(@PathVariable Long id,
                              @RequestParam("orderStatus") OrderStatus newStatus) {
        orderService.updateOrderStatus(
                UpdateOrderDTO.builder().id(id).status(newStatus).build()
        );
        return ORDERS_REDIRECT;
    }
}


