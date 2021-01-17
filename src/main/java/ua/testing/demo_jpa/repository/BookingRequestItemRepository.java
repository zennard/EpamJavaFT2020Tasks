package ua.testing.demo_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.BookingRequestItem;

@Repository
public interface BookingRequestItemRepository extends JpaRepository<BookingRequestItem, Long> {
}
