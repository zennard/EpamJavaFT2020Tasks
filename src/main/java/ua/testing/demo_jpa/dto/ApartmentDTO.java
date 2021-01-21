package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.RoomStatus;
import ua.testing.demo_jpa.entity.RoomType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApartmentDTO {
    Long apartmentId;
    Long apartmentTimetableId;
    Integer bedsCount;
    RoomType type;
    LocalDateTime startsAt;
    LocalDateTime endsAt;
    RoomStatus status;
    String description;
}
