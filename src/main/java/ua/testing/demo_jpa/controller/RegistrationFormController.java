package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.service.UserService;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationFormController {
    private UserService userService;

    @Autowired
    public RegistrationFormController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getRegistrationPage() {
        return "registration_form_controller/registration_form.html";
    }

    @PostMapping()
    public String registerUser(UserRegistrationDTO user) {
        userService.saveNewUser(user);
        log.info("{}", user);
        return "redirect:/users";
    }
}
