package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.testing.demo_jpa.auth.UserPrincipal;
import ua.testing.demo_jpa.dto.BookingRequestCreationDTO;
import ua.testing.demo_jpa.dto.BookingRequestItemDTO;
import ua.testing.demo_jpa.dto.VacationDateDTO;
import ua.testing.demo_jpa.entity.RoomType;
import ua.testing.demo_jpa.service.BookingRequestService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/booking-requests")
public class BookingRequestController {
    private static final String BOOKING_REQUEST_CREATION_PAGE = "booking_request_controller/booking_request_creation.html";
    private static final String BOOKING_REQUEST_CREATION_PAGE_REDIRECT = "redirect:/booking-requests/create";
    private static final int DEFAULT_DAYS_OFFSET = 3;
    private final BookingRequestService bookingRequestService;

    @Autowired
    public BookingRequestController(BookingRequestService bookingRequestService) {
        this.bookingRequestService = bookingRequestService;
    }

    @GetMapping
    public String getBookingRequestsPage(Model model) {
        return "";
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
    public String getBookingRequestPage(Model model) {
        return "";
    }

    @GetMapping(value = "/create")
    public String getBookingRequestCreationPage() {
        return BOOKING_REQUEST_CREATION_PAGE;
    }
}
