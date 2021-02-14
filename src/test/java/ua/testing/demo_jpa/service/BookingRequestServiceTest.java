package ua.testing.demo_jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ua.testing.demo_jpa.dto.BookingRequestDTO;
import ua.testing.demo_jpa.entity.*;
import ua.testing.demo_jpa.repository.BookingRequestItemRepository;
import ua.testing.demo_jpa.repository.BookingRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingRequestServiceTest {
    @Mock
    private BookingRequestRepository bookingRequestRepository;
    @Mock
    private BookingRequestItemRepository bookingRequestItemRepository;

    @InjectMocks
    private BookingRequestService testedInstance;

    @Test
    void shouldGetBookingRequestsByCorrectUserId() {
        //given
        when(bookingRequestRepository.findAllByUserId(any(), any()))
                .thenReturn(givenUserBookingRequests());
        when(bookingRequestItemRepository.findAllByBookingRequestId(any()))
                .thenReturn(Collections.singletonList(givenBookingRequestItems().get(0)),
                        Collections.singletonList(givenBookingRequestItems().get(1)));
        LocalDateTime startsAt = LocalDateTime.of(2020, 1, 10, 14, 0);
        LocalDateTime endsAt = LocalDateTime.of(2020, 1, 11, 12, 0);

        //when
        Page<BookingRequestDTO> bookingRequests = testedInstance.getAllBookingRequestsByUserId(null, 1L);
        //then
        assertThat(bookingRequests.getContent().get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("startsAt", startsAt)
                .hasFieldOrPropertyWithValue("endsAt", endsAt)
                .hasFieldOrPropertyWithValue("requestStatus", RequestStatus.NEW);
    }

    private List<BookingRequestItem> givenBookingRequestItems() {
        return Arrays.asList(
                BookingRequestItem.builder()
                        .id(1L)
                        .bookingRequest(BookingRequest.builder().id(1L).build())
                        .bedsCount(1)
                        .type(RoomType.STANDARD)
                        .build(),
                BookingRequestItem.builder()
                        .id(2L)
                        .bookingRequest(BookingRequest.builder().id(2L).build())
                        .bedsCount(2)
                        .type(RoomType.DELUXE)
                        .build()
        );
    }

    private Page<BookingRequest> givenUserBookingRequests() {
        return new PageImpl<>(Arrays.asList(
                BookingRequest.builder()
                        .id(1L)
                        .user(User.builder().id(1L).build())
                        .startsAt(LocalDateTime.of(2020, 1, 10, 14, 0))
                        .endsAt(LocalDateTime.of(2020, 1, 11, 12, 0))
                        .requestStatus(RequestStatus.NEW)
                        .requestDate(LocalDateTime.of(2020, 1, 1, 10, 10))
                        .build(),
                BookingRequest.builder()
                        .id(2L)
                        .user(User.builder().id(1L).build())
                        .startsAt(LocalDateTime.of(2020, 1, 11, 14, 0))
                        .endsAt(LocalDateTime.of(2020, 1, 12, 12, 0))
                        .requestStatus(RequestStatus.NEW)
                        .requestDate(LocalDateTime.of(2020, 1, 2, 10, 10))
                        .build()
        ), PageRequest.of(0, 2), 1);
    }

    @Test
    void shouldGetAllNewBookingRequests() {
        //given
        when(bookingRequestRepository.findAllByRequestStatus(any(), any()))
                .thenReturn(givenUserBookingRequests());
        when(bookingRequestItemRepository.findAllByBookingRequestId(any()))
                .thenReturn(Collections.singletonList(givenBookingRequestItems().get(0)),
                        Collections.singletonList(givenBookingRequestItems().get(1)));
        LocalDateTime startsAt = LocalDateTime.of(2020, 1, 10, 14, 0);
        LocalDateTime endsAt = LocalDateTime.of(2020, 1, 11, 12, 0);

        //when
        Page<BookingRequestDTO> bookingRequests = testedInstance.getAllNewBookingRequests(null);

        //then
        assertThat(bookingRequests.getContent().get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("startsAt", startsAt)
                .hasFieldOrPropertyWithValue("endsAt", endsAt)
                .hasFieldOrPropertyWithValue("requestStatus", RequestStatus.NEW);
    }


}