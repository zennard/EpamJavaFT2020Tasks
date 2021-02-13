package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegistrationFormController {
    private UserService userService;
    public static final String REG_FORM = "registration_form_controller/registration_form.html";

    @Autowired
    public RegistrationFormController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getRegistrationPage(@ModelAttribute("user") UserRegistrationDTO user,
                                      Principal principal) {
        if (principal != null) return "redirect:/users?page=0&size=2";
        return REG_FORM;
    }

    @PostMapping()
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDTO user,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "validation");
            return REG_FORM;
        }
        userService.saveNewUser(user);
        log.info("{}", user);
        return "redirect:/login";
    }
}
