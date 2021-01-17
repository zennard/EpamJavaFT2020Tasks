package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.entity.Apartment;
import ua.testing.demo_jpa.repository.ApartmentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    public Page<Apartment> getAllApartments(Pageable pageable) {
        return apartmentRepository.findAll(pageable);
    }

    public Page<Apartment> getAllApartmentsByDate(Pageable pageable,
                                                  LocalDateTime startsAt, LocalDateTime endsAt) {
        return apartmentRepository.findAllByDate(startsAt, endsAt, pageable);
    }

    public Page<Apartment> getAllAvailableApartmentsByDate(Pageable pageable,
                                                           LocalDateTime startsAt, LocalDateTime endsAt) {
        return apartmentRepository.findAllAvailableByDate(startsAt, endsAt, pageable);
    }

    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }
}
