package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ua.testing.demo_jpa.entity.Apartment;
import ua.testing.demo_jpa.entity.ApartmentTimetable;
import ua.testing.demo_jpa.exceptions.WrongTimetableIdException;
import ua.testing.demo_jpa.service.ApartmentService;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/apartments")
public class ApartmentController {
    private static final String APARTMENTS_PAGE = "apartment_controller/apartments.html";
    private final ApartmentService apartmentService;

    @Autowired
    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping()
    public String getApartmentsPage(Model model,
                                    @RequestParam(value = "startsAt", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startsAt,
                                    @RequestParam(value = "endsAt", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endsAt,
                                    @PageableDefault(sort = {"id"}, size = 2) Pageable pageable) {
        log.error("{}", startsAt);
        log.error("{}", endsAt);

        if (startsAt == null || endsAt == null) {
            startsAt = LocalDateTime.now().minusDays(3);
            endsAt = LocalDateTime.now().plusDays(3);
        }

        Page<Apartment> apartmentPage = apartmentService.getAllAvailableApartmentsByDate(pageable, startsAt, endsAt);
        Pageable currentPageable = apartmentPage.getPageable();
        int currentPageNum = currentPageable.getPageNumber();
        int prevPage = currentPageNum - 1;
        int nextPage = currentPageNum + 1;

        model.addAttribute("apartments", apartmentPage.getContent());
        model.addAttribute("currentPage", currentPageNum + 1);
        model.addAttribute("limit", currentPageable.getPageSize());
        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", apartmentPage.getTotalPages());
        model.addAttribute("hasPrev", apartmentPage.hasPrevious());
        model.addAttribute("hasNext", apartmentPage.hasNext());
        model.addAttribute("currentYear", LocalDateTime.now().getYear());
        model.addAttribute("nextYear", (LocalDateTime.now().getYear() + 1));
        model.addAttribute("checkIn", startsAt);
        model.addAttribute("checkOut", endsAt);

        return APARTMENTS_PAGE;
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getApartmentPage(Model model,
                                   @PathVariable("id") Long id,
                                   @RequestParam(name = "slotId", required = false) Long timeSlotId,
                                   @RequestParam(value = "startsAt", required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startsAt,
                                   @RequestParam(value = "endsAt", required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endsAt) {
        Apartment apartment = apartmentService.getApartmentByIdAndDate(id, startsAt, endsAt);

        log.error("{}", apartment);

        ApartmentTimetable schedule;
        if (timeSlotId == null) {
            schedule = apartment.getSchedule().get(0);
        } else {
            schedule = apartment
                    .getSchedule()
                    .stream()
                    .filter(t -> t.getId().equals(timeSlotId))
                    .findFirst()
                    .orElseThrow(() -> new WrongTimetableIdException("Cannot find record with this id"));
        }

        model.addAttribute("apartment", apartment);
        model.addAttribute("apartmentIds", Collections.singletonList(apartment.getId()));
        model.addAttribute("schedule", schedule);
        model.addAttribute("userStartsAt", startsAt);
        model.addAttribute("userEndsAt", endsAt);

        return "apartment_controller/apartment.html";
    }
}
