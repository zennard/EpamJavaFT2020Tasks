package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.dto.UserLoginDTO;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginFormController {
    public static final String LOGIN_FORM = "login_form_controller/login_form.html";

    @GetMapping()
    public String getLoginPage(@ModelAttribute("user") UserLoginDTO user,
                               Principal principal) {
        if (principal != null) return "redirect:/apartments";
        return LOGIN_FORM;
    }


}
