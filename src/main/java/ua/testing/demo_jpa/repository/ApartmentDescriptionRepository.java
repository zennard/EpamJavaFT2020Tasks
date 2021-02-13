package ua.testing.demo_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.ApartmentDescription;
import ua.testing.demo_jpa.entity.Language;

import java.util.Optional;

@Repository
public interface ApartmentDescriptionRepository extends JpaRepository<ApartmentDescription, Long> {
    Optional<ApartmentDescription> findApartmentDescriptionByApartmentIdAndLang(Long apartmentId, Language lang);
}
