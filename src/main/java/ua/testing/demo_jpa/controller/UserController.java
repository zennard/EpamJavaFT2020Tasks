package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.service.UserService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getUsersPage(Model model) {
        List<User> users = userService.getAllUsers().getUsers();
        model.addAttribute("users", users);
        return "user_controller/users.html";
    }
}
