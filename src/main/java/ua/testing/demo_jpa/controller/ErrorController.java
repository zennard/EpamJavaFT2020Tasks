package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import ua.testing.demo_jpa.auth.UserPrincipal;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.exceptions.*;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private static final String ERROR_PAGE = "error_controller/error.html";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        log.error(Arrays.toString(ex.getStackTrace()));
        log.error(ex.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex,
                                  Model model,
                                  Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex,
                                                 Model model,
                                                 Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(IllegalEmailException.class)
    public String handleIllegalEmailException(IllegalEmailException ex,
                                              Model model) {
        log.error(Arrays.toString(ex.getStackTrace()));
        log.error(ex.getMessage());
        model.addAttribute("error", "email");
        model.addAttribute("user", new UserRegistrationDTO());
        return "registration_form_controller/registration_form.html";
    }

    @ExceptionHandler(ApartmentNotFoundException.class)
    public String handleApartmentNotFoundException(ApartmentNotFoundException ex,
                                                   Model model,
                                                   Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(WrongTimetableIdException.class)
    public String wrongTimetableIdException(WrongTimetableIdException ex,
                                            Model model,
                                            Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(DescriptionNotFoundException.class)
    public String descriptionNotFoundException(DescriptionNotFoundException ex,
                                               Model model,
                                               Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String userNotFoundException(UserNotFoundException ex,
                                        Model model,
                                        Authentication authentication) {
        return processException(ex, model, authentication);
    }

    @ExceptionHandler(ForbiddenPageException.class)
    public String forbiddenPageException(ForbiddenPageException ex,
                                         Model model,
                                         Authentication authentication) {
        return processException(ex, model, authentication);
    }

    private String processException(Exception ex, Model model, Authentication authentication) {
        log.error(Arrays.toString(ex.getStackTrace()));
        log.error(ex.getMessage());
        model.addAttribute("exception", ex);
        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    model.addAttribute("userId", user.getUserId());
                });
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
