package ua.testing.demo_jpa.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ua.testing.demo_jpa.entity.ApartmentTimeSlotView;
import ua.testing.demo_jpa.entity.ApartmentTimetable;
import ua.testing.demo_jpa.entity.RoomStatus;
import ua.testing.demo_jpa.entity.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApartmentTimeSlotViewImpl implements ApartmentTimeSlotView {
    private Long id;
    private ApartmentTimetable slotId;
    private BigDecimal price;
    private Integer bedsCount;
    private RoomType type;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private RoomStatus status;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getSlotId() {
        return slotId.getId();
    }

    public void setSlotId(ApartmentTimetable slotId) {
        this.slotId = slotId;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public Integer getBedsCount() {
        return bedsCount;
    }

    public void setBedsCount(Integer bedsCount) {
        this.bedsCount = bedsCount;
    }

    @Override
    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    @Override
    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    @Override
    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    @Override
    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
