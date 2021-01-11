package ua.testing.demo_jpa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserLoginDTO {
    private String email;
    private String password;
}
