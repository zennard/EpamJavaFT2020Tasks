package ua.testing.demo_jpa.dto;

import lombok.*;
import ua.testing.demo_jpa.auth.RoleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserProfileDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private RoleType role;
}
