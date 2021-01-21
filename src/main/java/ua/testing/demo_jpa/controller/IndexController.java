package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
public class IndexController {
    @RequestMapping("/")
    public String mainPage(Principal principal) {
        if (principal != null) return "redirect:/apartments";
        return "index_controller/index.html";
    }
}

