package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.entity.Apartment;
import ua.testing.demo_jpa.entity.ApartmentTimeSlot;
import ua.testing.demo_jpa.entity.ApartmentTimetable;
import ua.testing.demo_jpa.entity.RoomStatus;
import ua.testing.demo_jpa.exceptions.ApartmentNotFoundException;
import ua.testing.demo_jpa.exceptions.IllegalDateException;
import ua.testing.demo_jpa.mapper.ApartmentMapper;
import ua.testing.demo_jpa.repository.ApartmentRepository;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ApartmentTimetableRepository apartmentTimetableRepository;

    public Page<Apartment> getAllApartments(Pageable pageable) {
        return apartmentRepository.findAll(pageable);
    }

    public Page<Apartment> getAllAvailableApartmentsByDate(Pageable pageable,
                                                           LocalDateTime startsAt, LocalDateTime endsAt) {
        if (endsAt.isBefore(startsAt)) throw new IllegalDateException("Check out time cannot go before check-in!");

        Page<ApartmentTimeSlot> apartmentsPage = apartmentRepository.findAllAvailableByDate(startsAt, endsAt, pageable);
        List<Apartment> parsedApartments = new ArrayList<>();

        for (ApartmentTimeSlot slot : apartmentsPage.getContent()) {
            Apartment a = ApartmentMapper.map(slot);

            log.error("{}", a);
            log.error("{}", a.getSchedule());
            log.error("\n---\n");

            List<ApartmentTimetable> schedule = a.getSchedule();
            if (schedule.isEmpty()) {
                schedule.add(ApartmentTimetable
                        .builder()
                        .status(RoomStatus.FREE)
                        .startsAt(startsAt)
                        .endsAt(endsAt)
                        .build());
            }

            parsedApartments.add(a);
        }

        return new PageImpl<>(parsedApartments, apartmentsPage.getPageable(),
                apartmentsPage.getTotalElements());
    }

    public Apartment getApartmentByIdAndDate(Long id, LocalDateTime startsAt, LocalDateTime endsAt) {
        //@TODO rewrite
        Apartment apartment = apartmentRepository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException("Cannot find apartment with id " + id));

        List<ApartmentTimetable> schedule = apartmentTimetableRepository
                .findAllByApartmentIdAndDate(startsAt, endsAt, apartment.getId());
        if (schedule.isEmpty()) {
            schedule.add(ApartmentTimetable
                    .builder()
                    .status(RoomStatus.FREE)
                    .build());
        }

        apartment.setSchedule(schedule);

        return apartment;
    }

    public List<OrderItemDTO> getAllApartmentsByIds(List<Long> ids) {
        List<Apartment> apartments = apartmentRepository.findAllById(ids);

        return apartments
                .stream()
                .flatMap(a -> Stream.of(OrderItemDTO
                        .builder()
                        .apartmentId(a.getId())
                        .price(a.getPrice())
                        .amount(1)
                        .build()))
                .collect(Collectors.toList());
    }
}
