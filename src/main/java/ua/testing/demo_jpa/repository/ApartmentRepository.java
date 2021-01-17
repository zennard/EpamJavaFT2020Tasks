package ua.testing.demo_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.Apartment;

import java.time.LocalDateTime;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    @Query("SELECT ap FROM Apartment ap INNER JOIN ApartmentTimetable AS at " +
            "ON ap.id = at.apartment.id " +
            "WHERE at.startsAt >= :startsAt AND at.endsAt <= :endsAt ")
    Page<Apartment> findAllByDate(LocalDateTime startsAt, LocalDateTime endsAt, Pageable pageable);

    @Query("SELECT ap FROM Apartment ap INNER JOIN ApartmentTimetable AS at " +
            "ON ap.id = at.apartment.id " +
            "WHERE at.startsAt >= :startsAt AND at.endsAt <= :endsAt AND ap.isAvailable = true")
    Page<Apartment> findAllAvailableByDate(LocalDateTime startsAt, LocalDateTime endsAt, Pageable pageable);
}
