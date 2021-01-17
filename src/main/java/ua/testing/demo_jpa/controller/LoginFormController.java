package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.service.UserService;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginFormController {
    public static final String LOGIN_FORM = "login_form_controller/login_form.html";
    private final UserService userService;

    @Autowired
    public LoginFormController(UserService userService) {
        this.userService = userService;
    }

    //    @ResponseStatus(HttpStatus.OK)
//    @PostMapping()
//    public String loginFormController(@ModelAttribute("user") @Valid UserLoginDTO user,
//                                      BindingResult bindingResult,
//                                      Model model) {
//        log.info("{}", userService.findByUserLogin(user));
//        log.info("{}", user);
//
//        if (!bindingResult.hasErrors()) {
//            if (!userService.findByUserLogin(user).isPresent()) {
//                log.error("jpa error");
//                model.addAttribute("error", "credentials");
//                return LOGIN_FORM;
//            }
//            log.info("successful login");
//            return "redirect:/users/";
//        } else {
//            log.error("binding res error");
//            model.addAttribute("error", "validation");
//            return LOGIN_FORM;
//        }
//    }

    @GetMapping()
    public String getLoginPage(@ModelAttribute("user") UserLoginDTO user,
                               Principal principal) {
        if (principal != null) return "redirect:/users?page=0&size=2";
        return LOGIN_FORM;
    }


}
