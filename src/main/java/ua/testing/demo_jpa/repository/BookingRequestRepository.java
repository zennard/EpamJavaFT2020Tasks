package ua.testing.demo_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.dto.BookingRequestUpdateDTO;
import ua.testing.demo_jpa.entity.BookingRequest;
import ua.testing.demo_jpa.entity.RequestStatus;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    Page<BookingRequest> findAllByRequestStatus(RequestStatus requestStatus, Pageable pageable);

    @Modifying
    @Query("UPDATE BookingRequest r" +
            " SET r.requestStatus = :#{#bookingRequestUpdateDTO.status} " +
            "WHERE r.id = :#{#bookingRequestUpdateDTO.id} ")
    void updateStatus(BookingRequestUpdateDTO bookingRequestUpdateDTO);
}
