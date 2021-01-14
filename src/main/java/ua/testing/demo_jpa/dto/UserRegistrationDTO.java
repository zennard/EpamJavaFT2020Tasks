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
public class UserRegistrationDTO {
    @NotEmpty(message = "{validation.error.name.empty}")
    @Size(min = 2, max = 30, message = "{validation.error.name.size}")
    private String firstName;

    @NotEmpty(message = "{validation.error.surname.empty}")
    @Size(min = 2, max = 30, message = "{validation.error.surname.size}")
    private String lastName;

    @NotEmpty(message = "{validation.error.email.empty}")
    @Email(message = "{validation.error.email.invalid}")
    private String email;


    @Size(min = 8, max = 40, message = "{validation.error.password.size}")
    private String password;
}
