package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.dto.ApartmentCriteriaDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.dto.VacationDateDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.exceptions.ApartmentNotFoundException;
import ua.testing.demo_jpa.exceptions.DescriptionNotFoundException;
import ua.testing.demo_jpa.exceptions.IllegalDateException;
import ua.testing.demo_jpa.repository.ApartmentDescriptionRepository;
import ua.testing.demo_jpa.repository.ApartmentRepository;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;
import ua.testing.demo_jpa.util.ApartmentMapper;
import ua.testing.demo_jpa.util.Internationalization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentTimetableRepository apartmentTimetableRepository;
    private final ApartmentDescriptionRepository apartmentDescriptionRepository;
    //@TODO move to some constants class
    @Value("${apartment.check.in.time}")
    private int checkInHours;
    @Value("${apartment.check.out.time}")
    private int checkOutHours;
    private static final int SETTLEMENT_MINUTES = 0;
    private static final Set<String> VALID_COLUMNS_FOR_ORDER_BY = Collections.unmodifiableSet(
            (Set<? extends String>) Stream
                    .of("id", "type", "price", "beds_count", "status")
                    .collect(Collectors.toCollection(HashSet::new)));

    public Page<Apartment> getAllApartments(Pageable pageable) {
        return apartmentRepository.findAll(pageable);
    }

    public Page<Apartment> getAllAvailableApartmentsByDate(Pageable pageable,
                                                           LocalDate startsAt, LocalDate endsAt) {
        if (endsAt.isBefore(startsAt)) throw new IllegalDateException("Check out time cannot go before check-in!");

        LocalDateTime checkIn = LocalDateTime.of(startsAt, LocalTime.of(checkInHours, SETTLEMENT_MINUTES));
        LocalDateTime checkOut = LocalDateTime.of(endsAt, LocalTime.of(checkOutHours, SETTLEMENT_MINUTES));

        pageable = encodeSortParameter(pageable);
        log.info("{}", pageable.getSort());
        Page<ApartmentTimeSlotView> apartmentsPage = apartmentRepository.findAllAvailableByDate(checkIn, checkOut, pageable);
        
        List<Apartment> parsedApartments = new ArrayList<>();
        for (ApartmentTimeSlotView slot : apartmentsPage.getContent()) {
            Apartment a = ApartmentMapper.map(slot);

            updateEmptySchedule(a, checkIn, checkOut);

            parsedApartments.add(a);
        }
        log.info("total: {}", apartmentsPage.getTotalElements());
        log.info("total: {}", apartmentsPage.getTotalPages());

        return new PageImpl<>(parsedApartments, apartmentsPage.getPageable(),
                apartmentsPage.getTotalElements());
    }

    public Page<Apartment> getAllAvailableApartmentsByDate(Pageable pageable, VacationDateDTO vacationDateDTO,
                                                           ApartmentCriteriaDTO apartmentCriteriaDTO) {
        LocalDate startsAt = vacationDateDTO.getStartsAt();
        LocalDate endsAt = vacationDateDTO.getEndsAt();

        if (endsAt.isBefore(startsAt)) throw new IllegalDateException("Check out time cannot go before check-in!");

        LocalDateTime checkIn = LocalDateTime.of(startsAt, LocalTime.of(checkInHours, SETTLEMENT_MINUTES));
        LocalDateTime checkOut = LocalDateTime.of(endsAt, LocalTime.of(checkOutHours, SETTLEMENT_MINUTES));

        List<String> typesStringList = apartmentCriteriaDTO.getTypes().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        log.info("{}", apartmentCriteriaDTO.getTypes());
        log.info(String.format("'%s'", apartmentCriteriaDTO.getStatus().toString()));

        Page<ApartmentTimeSlotView> apartmentsPage = apartmentRepository.findAllAvailableByDate(checkIn, checkOut, pageable,
                typesStringList,
                apartmentCriteriaDTO.getStatus().toString());

        log.info("{}", apartmentsPage.getContent());

        List<Apartment> parsedApartments = new ArrayList<>();
        for (ApartmentTimeSlotView slot : apartmentsPage.getContent()) {
            Apartment a = ApartmentMapper.map(slot);

            updateEmptySchedule(a, checkIn, checkOut);

            parsedApartments.add(a);
        }

        log.info("total: {}", apartmentsPage.getTotalElements());
        log.info("total: {}", apartmentsPage.getTotalPages());

        return new PageImpl<>(parsedApartments, apartmentsPage.getPageable(),
                apartmentsPage.getTotalElements());
    }

    public Page<Apartment> getAllAvailableApartmentsByDate(Pageable pageable, VacationDateDTO vacationDateDTO,
                                                           ApartmentCriteriaDTO apartmentCriteriaDTO) {
        LocalDate startsAt = vacationDateDTO.getStartsAt();
        LocalDate endsAt = vacationDateDTO.getEndsAt();

        if (endsAt.isBefore(startsAt)) throw new IllegalDateException("Check out time cannot go before check-in!");

        LocalDateTime checkIn = LocalDateTime.of(startsAt, LocalTime.of(checkInHours, SETTLEMENT_MINUTES));
        LocalDateTime checkOut = LocalDateTime.of(endsAt, LocalTime.of(checkOutHours, SETTLEMENT_MINUTES));

        List<String> typesStringList = apartmentCriteriaDTO.getTypes().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        log.info("{}", apartmentCriteriaDTO.getTypes());
        log.info(String.format("'%s'", apartmentCriteriaDTO.getStatus().toString()));

        Page<ApartmentTimeSlotView> apartmentsPage = apartmentRepository.findAllAvailableByDate(checkIn, checkOut, pageable,
                typesStringList,
                apartmentCriteriaDTO.getStatus().toString());

        log.info("{}", apartmentsPage.getContent());

        List<Apartment> parsedApartments = new ArrayList<>();
        for (ApartmentTimeSlotView slot : apartmentsPage.getContent()) {
            Apartment a = ApartmentMapper.map(slot);

            updateEmptySchedule(a, checkIn, checkOut);

            parsedApartments.add(a);
        }

        log.info("total: {}", apartmentsPage.getTotalElements());
        log.info("total: {}", apartmentsPage.getTotalPages());

        return new PageImpl<>(parsedApartments, apartmentsPage.getPageable(),
                apartmentsPage.getTotalElements());
    }

    private Pageable encodeSortParameter(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), JpaSort.unsafe(
                pageable.getSort()
                        .get()
                        .filter(o -> VALID_COLUMNS_FOR_ORDER_BY.contains(o.getProperty()))
                        .map(o -> "(" + o.getProperty() + "), id")
                        .collect(Collectors.joining())
        ));
    }

    public Apartment getApartmentByIdAndDate(Long id, LocalDate startsAt, LocalDate endsAt) {
        //@TODO rewrite
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException("Cannot find apartment with id " + id));

        LocalDateTime checkIn = LocalDateTime.of(startsAt, LocalTime.of(checkInHours, SETTLEMENT_MINUTES));
        LocalDateTime checkOut = LocalDateTime.of(endsAt, LocalTime.of(checkOutHours, SETTLEMENT_MINUTES));

        List<ApartmentTimetable> schedule = apartmentTimetableRepository
                .findAllByApartmentIdAndDate(checkIn, checkOut, apartment.getId());
        if (schedule.isEmpty()) {
            schedule.add(
                    ApartmentTimetable.builder()
                            .status(RoomStatus.FREE)
                            .build()
            );
        }
        log.info("{}", schedule);

        ApartmentDescription description = apartmentDescriptionRepository.findApartmentDescriptionByApartmentIdAndLang(id,
                Language.valueOf(Internationalization.getCurrentLocale().toString().toUpperCase(Locale.ROOT)))
                .orElseThrow(() -> new DescriptionNotFoundException("Cannot find description for apartment with id " + id));

        apartment.setSchedule(schedule);
        apartment.setDescription(description.getDescription());

        return apartment;
    }

    public List<OrderItemDTO> getAllApartmentsByIds(List<Long> ids) {
        List<Apartment> apartments = apartmentRepository.findAllById(ids);

        return apartments
                .stream()
                .map(a -> OrderItemDTO.builder()
                        .apartmentId(a.getId())
                        .price(a.getPrice())
                        .amount(1)
                        .build()
                )
                .collect(Collectors.toList());
    }

    private void updateEmptySchedule(Apartment a, LocalDateTime checkIn, LocalDateTime checkOut) {
        log.info("{}", a);
        log.info("{}", a.getSchedule());
        log.info("\n---\n");

        List<ApartmentTimetable> schedule = a.getSchedule();
        if (schedule.isEmpty()) {
            schedule.add(
                    ApartmentTimetable.builder()
                            .status(RoomStatus.FREE)
                            .startsAt(checkIn)
                            .endsAt(checkOut)
                            .build()
            );
        }
    }
}
