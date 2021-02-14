package ua.testing.demo_jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ua.testing.demo_jpa.dto.ApartmentCriteriaDTO;
import ua.testing.demo_jpa.dto.OrderItemDTO;
import ua.testing.demo_jpa.dto.VacationDateDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.ApartmentDescriptionRepository;
import ua.testing.demo_jpa.repository.ApartmentRepository;
import ua.testing.demo_jpa.repository.ApartmentTimetableRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private ApartmentTimetableRepository apartmentTimetableRepository;
    @Mock
    private ApartmentDescriptionRepository apartmentDescriptionRepository;

    @InjectMocks
    private ApartmentService testedInstance;

    @Test
    void shouldGetAllAvailableApartmentsByValidDate() {
        //given
        when(apartmentRepository.findAllAvailableByDate(any(), any(), any()))
                .thenReturn(givenApartmentsPage());
        LocalDate startsAt = LocalDate.of(2020, 1, 1);
        LocalDate endsAt = LocalDate.of(2020, 1, 2);

        //when
        Page<Apartment> apartments = testedInstance.getAllAvailableApartmentsByDate(PageRequest.of(0, 2, Sort.by("id")),
                startsAt, endsAt);
        //then
        assertThat(apartments.getContent().get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bedsCount", 2)
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(3000))
                .hasFieldOrPropertyWithValue("type", RoomType.DELUXE);

        assertThat(apartments.getContent().get(1).getSchedule().get(0))
                .hasFieldOrPropertyWithValue("status", RoomStatus.FREE);
    }

    private Page<ApartmentTimeSlotView> givenApartmentsPage() {
        return new PageImpl<>(Arrays.asList(
                ApartmentTimeSlotViewImpl.builder()
                        .id(1L)
                        .price(BigDecimal.valueOf(3000))
                        .bedsCount(2)
                        .type(RoomType.DELUXE)
                        .slotId(
                                getApartmentTimetable()
                        )
                        .build(),
                ApartmentTimeSlotViewImpl.builder()
                        .id(2L)
                        .price(BigDecimal.valueOf(1000))
                        .bedsCount(2)
                        .type(RoomType.STANDARD)
                        .build()
        ), PageRequest.of(0, 2), 1);
    }

    private ApartmentTimetable getApartmentTimetable() {
        return ApartmentTimetable.builder()
                .id(1L)
                .status(RoomStatus.PAID)
                .startsAt(LocalDateTime.of(2020, 1, 1, 14, 0))
                .endsAt(LocalDateTime.of(2020, 1, 2, 12, 0))
                .apartment(Apartment.builder().id(1L).build())
                .build();
    }

    private Page<ApartmentTimeSlotView> givenFreeApartments() {
        return new PageImpl<>(Arrays.asList(
                ApartmentTimeSlotViewImpl.builder()
                        .id(1L)
                        .price(BigDecimal.valueOf(3000))
                        .bedsCount(2)
                        .type(RoomType.DELUXE)
                        .build(),
                ApartmentTimeSlotViewImpl.builder()
                        .id(2L)
                        .price(BigDecimal.valueOf(1000))
                        .bedsCount(2)
                        .type(RoomType.STANDARD)
                        .build()
        ), PageRequest.of(0, 2), 1);
    }

    @Test
    void shouldGetAllAvailableApartmentsByCorrectDateAndCriteria() {
        //given
        when(apartmentRepository.findAllAvailableByDate(any(), any(), any(), any(), any()))
                .thenReturn(givenFreeApartments());
        LocalDate startsAt = LocalDate.of(2020, 1, 1);
        LocalDate endsAt = LocalDate.of(2020, 1, 2);
        //when
        Page<Apartment> apartments = testedInstance.getAllAvailableApartmentsByDate(PageRequest.of(0, 2),
                new VacationDateDTO(startsAt, endsAt),
                new ApartmentCriteriaDTO(Arrays.asList(RoomType.STANDARD, RoomType.DELUXE), RoomStatus.FREE));
        //then
        assertThat(apartments.getContent().get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bedsCount", 2)
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(3000))
                .hasFieldOrPropertyWithValue("type", RoomType.DELUXE);

        assertThat(apartments.getContent().get(0).getSchedule().get(0))
                .hasFieldOrPropertyWithValue("status", RoomStatus.FREE);

        assertThat(apartments.getContent().get(1).getSchedule().get(0))
                .hasFieldOrPropertyWithValue("status", RoomStatus.FREE);
    }

    @Test
    void shouldGetApartmentByCorrectIdAndDate() {
        //given
        when(apartmentRepository.findById(any()))
                .thenReturn(givenApartment());
        when(apartmentTimetableRepository.findAllByApartmentIdAndDate(any(), any(), any()))
                .thenReturn(givenSchedule());
        when(apartmentDescriptionRepository.findApartmentDescriptionByApartmentIdAndLang(any(), any()))
                .thenReturn(givenDescription());
        LocalDate startsAt = LocalDate.of(2020, 1, 1);
        LocalDate endsAt = LocalDate.of(2020, 1, 4);
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        //when
        Apartment apartment = testedInstance.getApartmentByIdAndDate(1L,
                startsAt, endsAt);
        //then
        assertThat(apartment)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "desc en")
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(5000))
                .hasFieldOrPropertyWithValue("type", RoomType.PRESIDENT)
                .hasFieldOrPropertyWithValue("bedsCount", 1);
    }

    private Optional<ApartmentDescription> givenDescription() {
        return Optional.of(
                ApartmentDescription.builder()
                        .id(1L)
                        .apartment(Apartment.builder().id(1L).build())
                        .lang(Language.EN)
                        .description("desc en")
                        .build()
        );
    }

    private List<ApartmentTimetable> givenSchedule() {
        return Arrays.asList(
                ApartmentTimetable.builder()
                        .id(1L)
                        .apartment(Apartment.builder().id(1L).build())
                        .startsAt(LocalDateTime.of(2020, 1, 1, 14, 0))
                        .endsAt(LocalDateTime.of(2020, 1, 2, 12, 0))
                        .status(RoomStatus.PAID)
                        .build(),
                ApartmentTimetable.builder()
                        .id(2L)
                        .apartment(Apartment.builder().id(1L).build())
                        .startsAt(LocalDateTime.of(2020, 1, 3, 14, 0))
                        .endsAt(LocalDateTime.of(2020, 1, 4, 12, 0))
                        .status(RoomStatus.BOOKED)
                        .build()
        );
    }

    private Optional<Apartment> givenApartment() {
        return Optional.of(
                Apartment.builder()
                        .id(1L)
                        .type(RoomType.PRESIDENT)
                        .price(BigDecimal.valueOf(5000))
                        .bedsCount(1)
                        .isAvailable(true)
                        .build()
        );
    }

    @Test
    void shouldReturnApartmentsByIds() {
        //given
        when(apartmentRepository.findAllById(any()))
                .thenReturn(givenApartments());

        //when
        List<OrderItemDTO> apartments = testedInstance.getAllApartmentsByIds(Arrays.asList(1L, 2L));
        //then
        assertThat(apartments.get(0))
                .hasFieldOrPropertyWithValue("apartmentId", 1L)
                .hasFieldOrPropertyWithValue("price", BigDecimal.valueOf(3000));
    }

    private List<Apartment> givenApartments() {
        return Arrays.asList(
                Apartment.builder()
                        .id(1L)
                        .type(RoomType.PRESIDENT)
                        .price(BigDecimal.valueOf(3000))
                        .bedsCount(1)
                        .isAvailable(true)
                        .build(),
                Apartment.builder()
                        .id(2L)
                        .type(RoomType.STANDARD)
                        .price(BigDecimal.valueOf(1000))
                        .bedsCount(1)
                        .isAvailable(true)
                        .build()
        );
    }
}