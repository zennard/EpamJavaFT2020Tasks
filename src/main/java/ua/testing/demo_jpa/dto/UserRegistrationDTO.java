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
    @NotEmpty(message = "Name should not be empty!")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters!")
    private String firstName;

    @NotEmpty(message = "Surname should not be empty!")
    @Size(min = 2, max = 30, message = "Surname should be between 2 and 30 characters!")
    private String lastName;

    @NotEmpty(message = "Email should not be empty!")
    @Email(message = "Email should be valid!")
    private String email;


    @Size(min = 8, max = 40, message = "Password should be between 8 and 40 characters!")
    private String password;
}
