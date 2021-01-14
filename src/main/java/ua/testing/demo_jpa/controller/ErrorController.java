package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.exceptions.IllegalEmailException;

@Slf4j
@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        return "error_controller/error.html";
    }

    @ExceptionHandler(IllegalEmailException.class)
    public String handleIllegalEmailException(IllegalEmailException ex,
                                              Model model) {
        model.addAttribute("error", "email");
        model.addAttribute("user", new UserRegistrationDTO());
        return "registration_form_controller/registration_form.html";
    }
    

}
