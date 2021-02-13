package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.entity.RoomStatus;
import ua.testing.demo_jpa.entity.RoomType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApartmentCriteriaDTO {
    private List<RoomType> types;
    private RoomStatus status;
}
