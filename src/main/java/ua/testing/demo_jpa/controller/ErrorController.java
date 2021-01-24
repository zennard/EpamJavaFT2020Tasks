package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.exceptions.*;

@Slf4j
@ControllerAdvice
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private static final String ERROR_PAGE = "error_controller/error.html";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(IllegalEmailException.class)
    public String handleIllegalEmailException(IllegalEmailException ex,
                                              Model model) {
        log.error(ex.getMessage());
        model.addAttribute("error", "email");
        model.addAttribute("user", new UserRegistrationDTO());
        return "registration_form_controller/registration_form.html";
    }

    @ExceptionHandler(OrderDeletionException.class)
    public String handleOrderDeletionException(OrderDeletionException ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(ApartmentNotFoundException.class)
    public String handleApartmentNotFoundException(ApartmentNotFoundException ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(WrongTimetableIdException.class)
    public String wrongTimetableIdException(WrongTimetableIdException ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(DescriptionNotFoundException.class)
    public String descriptionNotFoundException(DescriptionNotFoundException ex) {
        log.error(ex.getMessage());
        return ERROR_PAGE;
    }

    @Override
    public String getErrorPath() {
        return "redirect:/error";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return ERROR_PAGE;
    }
}
