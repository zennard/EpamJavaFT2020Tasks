package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.BookingRequestCreationDTO;
import ua.testing.demo_jpa.dto.BookingRequestItemDTO;
import ua.testing.demo_jpa.entity.BookingRequest;
import ua.testing.demo_jpa.entity.BookingRequestItem;
import ua.testing.demo_jpa.entity.RequestStatus;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.repository.BookingRequestItemRepository;
import ua.testing.demo_jpa.repository.BookingRequestRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingRequestService {
    @Value("${apartment.check.in.time}")
    private Integer checkInHours;
    @Value("${apartment.check.out.time}")
    private Integer checkOutHours;
    private static final int SETTLEMENT_MINUTES = 0;
    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRequestItemRepository bookingRequestItemRepository;

    @Transactional
    public void saveRequest(BookingRequestCreationDTO bookingRequestCreationDTO) {
        User user = new User();
        user.setId(bookingRequestCreationDTO.getUserId());

        LocalDateTime startsAt = LocalDateTime.of(bookingRequestCreationDTO.getStartsAt(), LocalTime.of(checkInHours, SETTLEMENT_MINUTES));
        LocalDateTime endsAt = LocalDateTime.of(bookingRequestCreationDTO.getEndsAt(), LocalTime.of(checkOutHours, SETTLEMENT_MINUTES));

        BookingRequest request = BookingRequest.builder()
                .startsAt(startsAt)
                .endsAt(endsAt)
                .requestDate(LocalDateTime.now())
                .user(user)
                .requestStatus(RequestStatus.NEW)
                .build();

        List<BookingRequestItem> items = new ArrayList<>();
        for (BookingRequestItemDTO itemDTO : bookingRequestCreationDTO.getRequestItems()) {
            items.add(BookingRequestItem.builder()
                    .bookingRequest(request)
                    .bedsCount(itemDTO.getBedsCount())
                    .type(itemDTO.getType())
                    .build());
        }

        saveBookingRequest(request, items);
    }

    @Transactional
    public void saveBookingRequest(BookingRequest request, List<BookingRequestItem> items) {
        bookingRequestRepository.save(request);
        bookingRequestItemRepository.saveAll(items);
    }

    public Page<BookingRequest> findAllRequests(Pageable pageable) {
        return bookingRequestRepository.findAll(pageable);
    }

    public Optional<BookingRequest> findRequestById(Long requestId) {
        return bookingRequestRepository.findById(requestId);
    }
}
