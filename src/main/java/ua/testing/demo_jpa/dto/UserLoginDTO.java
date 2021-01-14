package ua.testing.demo_jpa.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserLoginDTO {
    @NotEmpty(message = "{validation.error.email.empty}")
    @Email(message = "{validation.error.email.invalid}")
    private String email;
    @Size(min = 8, max = 40, message = "{validation.error.password.size}")
    private String password;
}
