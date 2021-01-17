package ua.testing.demo_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.ApartmentTimetable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentTimetableRepository extends JpaRepository<ApartmentTimetable, Long> {
    List<ApartmentTimetable> findAllByStartsAtGreaterThanEqualAndEndsAtLessThanEqual(
            LocalDateTime startsAt, LocalDateTime endsAt);

    Optional<ApartmentTimetable> findByStartsAtAndEndsAtAndApartmentId(
            LocalDateTime startsAt, LocalDateTime endsAt, Long apartmentId
    );
}
