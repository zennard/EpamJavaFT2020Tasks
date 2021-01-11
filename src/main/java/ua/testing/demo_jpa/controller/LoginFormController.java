package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.service.UserService;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginFormController {
    private final UserService userService;

    @Autowired
    public LoginFormController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public String loginFormController(UserLoginDTO user) {
        log.info("{}", userService.findByUserLogin(user));
        log.info("{}", user);

        if (!userService.findByUserLogin(user).isPresent()) {
            return "redirect:/login?error=wrong_data";
        }
        return "redirect:/users/";
    }

    @GetMapping()
    public String getLoginPage(@RequestParam(name = "error", required = false) String error) {
        //@TODO use some template engine to show err
        if (error != null) {
            log.error(error);
        }
        return "login_form_controller/login_form.html";
    }


}
