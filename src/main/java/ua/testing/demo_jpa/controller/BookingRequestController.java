package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.testing.demo_jpa.auth.UserPrincipal;
import ua.testing.demo_jpa.dto.*;
import ua.testing.demo_jpa.entity.Apartment;
import ua.testing.demo_jpa.entity.RequestStatus;
import ua.testing.demo_jpa.entity.RoomStatus;
import ua.testing.demo_jpa.entity.RoomType;
import ua.testing.demo_jpa.exceptions.BookingRequestNotFound;
import ua.testing.demo_jpa.exceptions.UserNotFoundException;
import ua.testing.demo_jpa.service.ApartmentService;
import ua.testing.demo_jpa.service.BookingRequestService;
import ua.testing.demo_jpa.service.OrderService;
import ua.testing.demo_jpa.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/booking-requests")
public class BookingRequestController {
    private static final String BOOKING_REQUEST_CREATION_PAGE = "booking_request_controller/booking_request_creation.html";
    private static final String BOOKING_REQUEST_CREATION_PAGE_REDIRECT = "redirect:/booking-requests/create";
    private static final String BOOKING_REQUESTS_PAGE = "booking_request_controller/booking_requests.html";
    private static final String BOOKING_REQUEST_PAGE = "booking_request_controller/booking_request.html";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found by id: ";
    private static final String BOOKING_REQUEST_NOT_FOUND_EXCEPTION_MESSAGE = "Booking request not found by id";
    private static final int DEFAULT_DAYS_OFFSET = 3;
    private final BookingRequestService bookingRequestService;
    private final ApartmentService apartmentService;
    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public BookingRequestController(BookingRequestService bookingRequestService, ApartmentService apartmentService, OrderService orderService, UserService userService) {
        this.bookingRequestService = bookingRequestService;
        this.apartmentService = apartmentService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String getBookingRequestsPage(Model model,
                                         @PageableDefault(sort = {"id"}, size = 2) Pageable pageable,
                                         Authentication authentication) {
        Page<BookingRequestDTO> requestsPage = bookingRequestService.getAllNewBookingRequests(pageable);

        model.addAttribute("requests", requestsPage.getContent());
        model.addAttribute("page", getPageDTO(requestsPage));

        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    model.addAttribute("userId", user.getUserId());
                });

        return BOOKING_REQUESTS_PAGE;
    }

    private PageDTO getPageDTO(Page<BookingRequestDTO> apartmentPage) {
        Pageable currentPageable = apartmentPage.getPageable();
        return PageDTO.builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(apartmentPage.getTotalPages())
                .hasPrev(apartmentPage.hasPrevious())
                .hasNext(apartmentPage.hasNext())
                .url("/booking-requests")
                .build();
    }

    private PageDTO getPageDTO(Page<Apartment> apartmentPage, Long requestId) {
        Pageable currentPageable = apartmentPage.getPageable();
        return PageDTO.builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(apartmentPage.getTotalPages())
                .hasPrev(apartmentPage.hasPrevious())
                .hasNext(apartmentPage.hasNext())
                .url("/booking-requests/" + requestId)
                .build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createBookingRequest(
            @RequestParam(name = "bedsCountInput") List<Integer> bedsCountInputList,
            @RequestParam(name = "typeSelect") List<RoomType> typesList,
            @RequestParam(value = "startsAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startsAt,
            @RequestParam(value = "endsAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endsAt,
            Authentication authentication) {
        if (startsAt == null || endsAt == null) {
            startsAt = LocalDate.now().minusDays(DEFAULT_DAYS_OFFSET);
            endsAt = LocalDate.now().plusDays(DEFAULT_DAYS_OFFSET);
        }
        log.info("{}", bedsCountInputList);
        log.info("{}", typesList);

        Long userId = Optional.ofNullable(authentication)
                .map(auth -> (UserPrincipal) auth.getPrincipal())
                .map(UserPrincipal::getUserId)
                .orElse(null);

        VacationDateDTO vacationDateDTO = new VacationDateDTO(startsAt, endsAt);
        bookingRequestService.saveRequest(
                BookingRequestCreationDTO.builder()
                        .userId(userId)
                        .startsAt(vacationDateDTO.getStartsAt())
                        .endsAt(vacationDateDTO.getEndsAt())
                        .requestItems(getBookingRequestItemsDTOList(bedsCountInputList, typesList))
                        .build()
        );
        return BOOKING_REQUEST_CREATION_PAGE_REDIRECT;
    }

    private List<BookingRequestItemDTO> getBookingRequestItemsDTOList(List<Integer> bedsCountInputList, List<RoomType> typesList) {
        List<BookingRequestItemDTO> items = new ArrayList<>();

        for (int i = 0; i < bedsCountInputList.size(); i++) {
            items.add(
                    BookingRequestItemDTO.builder()
                            .bedsCount(bedsCountInputList.get(i))
                            .type(typesList.get(i))
                            .build()
            );
        }

        return items;
    }

    @GetMapping(value = "/{id}")
    public String getBookingRequestPage(Model model,
                                        @PathVariable Long id,
                                        @PageableDefault(sort = {"id"}, size = 2) Pageable pageable,
                                        Authentication authentication) {
        BookingRequestDTO bookingRequest = bookingRequestService.findRequestById(id)
                .orElseThrow(() -> new BookingRequestNotFound(BOOKING_REQUEST_NOT_FOUND_EXCEPTION_MESSAGE));
        List<RoomType> types = getUniqueTypes(bookingRequest.getRequestItems());
        Page<Apartment> apartmentPage = apartmentService.getAllAvailableApartmentsByDate(pageable,
                new VacationDateDTO(bookingRequest.getStartsAt().toLocalDate(),
                        bookingRequest.getEndsAt().toLocalDate()),
                new ApartmentCriteriaDTO(types, RoomStatus.FREE));

        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("apartments", apartmentPage.getContent());
        model.addAttribute("page", getPageDTO(apartmentPage, id));
        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    model.addAttribute("userId", user.getUserId());
                });

        return BOOKING_REQUEST_PAGE;
    }

    private List<RoomType> getUniqueTypes(List<BookingRequestItemDTO> items) {
        Set<RoomType> roomSet = new HashSet<>();
        items.forEach(item -> roomSet.add(item.getType()));
        return new ArrayList<>(roomSet);
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateBookingRequest(@RequestParam(name = "bookingStatus") RequestStatus status,
                                       @RequestParam(name = "userId") Long userId,
                                       @PathVariable Long id,
                                       @RequestParam List<Long> apartmentIds,
                                       @RequestParam(value = "startsAt", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startsAt,
                                       @RequestParam(value = "endsAt", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endsAt) {
        bookingRequestService.updateRequestStatus(
                BookingRequestUpdateDTO.builder()
                        .id(id)
                        .status(status)
                        .build()
        );

        if (status.equals(RequestStatus.CLOSED)) {
            List<OrderItemDTO> items = apartmentService.getAllApartmentsByIds(apartmentIds);
            UserProfileDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE + userId));

            OrderCreationDTO orderDTO = OrderCreationDTO
                    .builder()
                    .userEmail(user.getEmail())
                    .orderDate(LocalDateTime.now())
                    .startsAt(startsAt)
                    .endsAt(endsAt)
                    .orderItems(items)
                    .build();
            orderService.createNewOrder(orderDTO);
        }
        return String.format("redirect:/apartments?startsAt=%s&endsAt=%s",
                startsAt.toLocalDate(), endsAt.toLocalDate());
    }

    @GetMapping(value = "/create")
    public String getBookingRequestCreationPage(Model model, Authentication authentication) {
        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    model.addAttribute("userId", user.getUserId());
                });

        return BOOKING_REQUEST_CREATION_PAGE;
    }
}
