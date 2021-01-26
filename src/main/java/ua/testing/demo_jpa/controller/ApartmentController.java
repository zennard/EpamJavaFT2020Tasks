package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.testing.demo_jpa.dto.ApartmentPageContextDTO;
import ua.testing.demo_jpa.dto.DateDTO;
import ua.testing.demo_jpa.dto.PageDTO;
import ua.testing.demo_jpa.entity.Apartment;
import ua.testing.demo_jpa.entity.ApartmentTimetable;
import ua.testing.demo_jpa.exceptions.WrongTimetableIdException;
import ua.testing.demo_jpa.service.ApartmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/apartments")
public class ApartmentController {
    private static final String APARTMENTS_PAGE = "apartment_controller/apartments.html";
    private static final String APARTMENT_PAGE = "apartment_controller/apartment.html";
    private static final int SETTLEMENT_MINUTES = 0;
    public static final int DEFAULT_DAYS_OFFSET = 3;
    private final ApartmentService apartmentService;
    @Value("${apartment.check.in.time}")
    private Integer checkInHours;
    @Value("${apartment.check.out.time}")
    private Integer checkOutHours;

    @Autowired
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping()
    public String getApartmentsPage(Model model,
                                    @RequestParam(value = "startsAt", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startsAt,
                                    @RequestParam(value = "endsAt", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endsAt,
                                    @PageableDefault(sort = {"id"}, size = 2) Pageable pageable) {
        log.info("{}", startsAt);
        log.info("{}", endsAt);

        if (startsAt == null || endsAt == null) {
            startsAt = LocalDate.now().minusDays(DEFAULT_DAYS_OFFSET);
            endsAt = LocalDate.now().plusDays(DEFAULT_DAYS_OFFSET);
        }

        Page<Apartment> apartmentPage = apartmentService.getAllAvailableApartmentsByDate(pageable, startsAt, endsAt);
        Pageable currentPageable = apartmentPage.getPageable();

        model.addAttribute("pageContext",
                ApartmentPageContextDTO.builder()
                        .apartments(apartmentPage.getContent())
                        .page(getPageDTO(apartmentPage, currentPageable))
                        .date(getDateDTO(startsAt, endsAt))
                        .build()
        );

        return APARTMENTS_PAGE;
    }

    private DateDTO getDateDTO(LocalDate startsAt, LocalDate endsAt) {
        return DateDTO.builder()
                .prevYear(LocalDateTime.now().getYear() - 1)
                .nextYear(LocalDateTime.now().getYear() + 1)
                .checkIn(startsAt)
                .checkOut(endsAt)
                .build();
    }

    private PageDTO getPageDTO(Page<Apartment> apartmentPage, Pageable currentPageable) {
        return PageDTO.builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(apartmentPage.getTotalPages())
                .hasPrev(apartmentPage.hasPrevious())
                .hasNext(apartmentPage.hasNext())
                .url("/apartments")
                .build();
    }

    //@TODO rewrite
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getApartmentPage(Model model,
                                   @PathVariable("id") Long id,
                                   @RequestParam(name = "slotId", required = false) Long timeSlotId,
                                   @RequestParam(value = "startsAt", required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startsAt,
                                   @RequestParam(value = "endsAt", required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endsAt) {
        Apartment apartment = apartmentService.getApartmentByIdAndDate(id, startsAt, endsAt);

        log.info("{}", apartment);

        ApartmentTimetable schedule = Optional.ofNullable(timeSlotId)
                .map(tId -> apartment
                        .getSchedule()
                        .stream()
                        .filter(t -> t.getId().equals(tId))
                        .findFirst()
                        .orElseThrow(() -> new WrongTimetableIdException("Cannot find record with this id")))
                .orElseGet(() -> apartment.getSchedule().get(0));

        model.addAttribute("apartment", apartment);
        model.addAttribute("apartmentIds", Collections.singletonList(apartment.getId()));
        model.addAttribute("schedule", schedule);
        model.addAttribute("userStartsAt", LocalDateTime.of(startsAt,
                LocalTime.of(checkInHours, SETTLEMENT_MINUTES)));
        model.addAttribute("userEndsAt", LocalDateTime.of(endsAt,
                LocalTime.of(checkOutHours, SETTLEMENT_MINUTES)));

        return APARTMENT_PAGE;
    }
}
