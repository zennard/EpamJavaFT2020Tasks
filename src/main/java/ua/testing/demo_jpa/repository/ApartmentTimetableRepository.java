package ua.testing.demo_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.ApartmentTimetable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentTimetableRepository extends JpaRepository<ApartmentTimetable, Long> {
    List<ApartmentTimetable> findAllByStartsAtGreaterThanEqualAndEndsAtLessThanEqual(
            LocalDateTime startsAt, LocalDateTime endsAt);

    @Query("SELECT t FROM ApartmentTimetable t " +
            "WHERE t.apartment.id = :apartmentId AND (" +
            " t.startsAt BETWEEN :startsAt AND :endsAt OR " +
            " t.endsAt BETWEEN :startsAt AND :endsAt OR " +
            " t.startsAt <= :startsAt AND t.endsAt >= :endsAt " +
            ")"
    )
    List<ApartmentTimetable> findAllByApartmentIdAndDate(
            LocalDateTime startsAt, LocalDateTime endsAt, Long apartmentId);

    Optional<ApartmentTimetable> findByStartsAtAndEndsAtAndApartmentId(
            LocalDateTime startsAt, LocalDateTime endsAt, Long apartmentId
    );


}
