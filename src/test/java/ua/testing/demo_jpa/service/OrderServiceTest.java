package ua.testing.demo_jpa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.testing.demo_jpa.entity.RoomStatus;

import java.time.LocalDateTime;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService service;
    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2021, 1, 15, 13, 0);

    @Test
    void shouldSaveNewRecord() {
        LocalDateTime date = TEST_DATE;
        ApartmentTimetableDTO schedule = ApartmentTimetableDTO
                .builder()
                .apartmentId(3L)
                .startsAt(date)
                .endsAt(date.plusDays(2))
                .status(RoomStatus.PAID)
                .build();

        service.saveNewRecord(schedule);
    }

    @Test
    void shouldDeleteExistingRecordWithTimetableDTO() {
        LocalDateTime date = TEST_DATE;
        ApartmentTimetableDTO schedule = ApartmentTimetableDTO
                .builder()
                .apartmentId(3L)
                .startsAt(date)
                .endsAt(date.plusDays(2))
                .status(RoomStatus.PAID)
                .build();
        service.deleteRecord(schedule);
    }

    @Test
    void shouldDeleteExistingRecordWithTimetableDeletionDTO() {
        LocalDateTime date = TEST_DATE;
        ApartmentTimetableDeletionDTO schedule = ApartmentTimetableDeletionDTO
                .builder()
                .apartmentTimetableId(7L)
                .apartmentId(3L)
                .startsAt(date)
                .endsAt(date.plusDays(2))
                .build();
        service.deleteRecord(schedule);
    }
}