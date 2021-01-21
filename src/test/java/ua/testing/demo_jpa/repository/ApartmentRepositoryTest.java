package ua.testing.demo_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ua.testing.demo_jpa.entity.ApartmentTimeSlot;

import java.time.LocalDateTime;
import java.util.List;

//@SpringBootTest
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApartmentRepositoryTest {
    @Autowired
    private ApartmentRepository repository;

    @Test
    void shouldFindAllByDate() {
        LocalDateTime date = LocalDateTime.of(2021, 1, 15, 13, 0);
        Page<ApartmentTimeSlot> apartmentsPage = repository.findAllAvailableByDate(date,
                date.plusDays(2),
                PageRequest.of(0, 10));
        List<ApartmentTimeSlot> apartments = apartmentsPage.getContent();
        System.err.println(apartments);
        if (!apartments.isEmpty()) {
            System.err.println(apartments.get(0));
        }
    }
}