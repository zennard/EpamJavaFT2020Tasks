package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.BookingRequestDTO;
import ua.testing.demo_jpa.dto.BookingRequestItemDTO;
import ua.testing.demo_jpa.entity.BookingRequest;
import ua.testing.demo_jpa.entity.BookingRequestItem;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.repository.BookingRequestItemRepository;
import ua.testing.demo_jpa.repository.BookingRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingRequestService {
    private BookingRequestRepository bookingRequestRepository;
    private BookingRequestItemRepository bookingRequestItemRepository;

    @Transactional
    public void saveRequest(BookingRequestDTO bookingRequestDTO) {
        User user = new User();
        user.setId(bookingRequestDTO.getUserId());
        BookingRequest request = BookingRequest
                .builder()
                .startsAt(bookingRequestDTO.getStartsAt())
                .endsAt(bookingRequestDTO.getEndsAt())
                .requestDate(LocalDateTime.now())
                .user(user)
                .build();
        bookingRequestRepository.save(request);

        for (BookingRequestItemDTO itemDTO : bookingRequestDTO.getRequestItems()) {
            bookingRequestItemRepository.save(BookingRequestItem
                    .builder()
                    .bookingRequest(request)
                    .beds_count(itemDTO.getBedsCount())
                    .type(itemDTO.getType())
                    .build());
        }
    }

    public Page<BookingRequest> findAllRequests(Pageable pageable) {
        return bookingRequestRepository.findAll(pageable);
    }

    public Optional<BookingRequest> findRequestById(Long requestId) {
        return bookingRequestRepository.findById(requestId);
    }
}
