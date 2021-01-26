package ua.testing.demo_jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ApartmentTimeSlotView {
    Long getId();

    Long getSlotId();

    BigDecimal getPrice();

    Integer getBedsCount();

    RoomType getType();

    LocalDateTime getStartsAt();

    LocalDateTime getEndsAt();

    RoomStatus getStatus();
}
