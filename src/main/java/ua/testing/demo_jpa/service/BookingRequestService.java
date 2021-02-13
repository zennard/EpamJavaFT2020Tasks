package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.testing.demo_jpa.dto.BookingRequestCreationDTO;
import ua.testing.demo_jpa.dto.BookingRequestDTO;
import ua.testing.demo_jpa.dto.BookingRequestItemDTO;
import ua.testing.demo_jpa.dto.BookingRequestUpdateDTO;
import ua.testing.demo_jpa.entity.BookingRequest;
import ua.testing.demo_jpa.entity.BookingRequestItem;
import ua.testing.demo_jpa.entity.RequestStatus;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.exceptions.EmptyBookingRequestException;
import ua.testing.demo_jpa.repository.BookingRequestItemRepository;
import ua.testing.demo_jpa.repository.BookingRequestRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    public Optional<BookingRequestDTO> findRequestById(Long requestId) {
        BookingRequest bookingRequest = bookingRequestRepository.findById(requestId).orElse(null);

        if (bookingRequest == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(getBookingRequestDTO(bookingRequest));
    }

    public Page<BookingRequestDTO> getAllNewBookingRequests(Pageable pageable) {
        Page<BookingRequest> requestsPage = bookingRequestRepository.findAllByRequestStatus(RequestStatus.NEW, pageable);

        List<BookingRequestDTO> requestsDTO = requestsPage.getContent().stream()
                .map(this::getBookingRequestDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(requestsDTO, requestsPage.getPageable(),
                requestsPage.getTotalPages());
    }

    private BookingRequestDTO getBookingRequestDTO(BookingRequest request) {
        List<BookingRequestItem> items;
        items = bookingRequestItemRepository.findAllByBookingRequestId(request.getId());

        if (items.isEmpty()) {
            throw new EmptyBookingRequestException("Cannot create request without request items!");
        }

        List<BookingRequestItemDTO> itemsDTO = items.stream()
                .map(this::getBookingRequestItemDTO)
                .collect(Collectors.toList());

        return BookingRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .requestDate(request.getRequestDate())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .requestStatus(request.getRequestStatus())
                .requestItems(itemsDTO)
                .build();
    }

    private BookingRequestItemDTO getBookingRequestItemDTO(BookingRequestItem item) {
        return BookingRequestItemDTO.builder()
                .bedsCount(item.getBedsCount())
                .type(item.getType())
                .build();
    }

    @Transactional
    public void updateRequestStatus(BookingRequestUpdateDTO bookingRequestUpdateDTO) {
        log.info("{}", bookingRequestUpdateDTO);
        bookingRequestRepository.updateStatus(bookingRequestUpdateDTO);
    }
}
