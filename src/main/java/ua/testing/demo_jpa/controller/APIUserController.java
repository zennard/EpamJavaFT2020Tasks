package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.testing.demo_jpa.dto.UsersDTO;
import ua.testing.demo_jpa.service.UserService;

@Slf4j
@RestController
@RequestMapping("api/users")
public class APIUserController {
    private UserService userService;

    @Autowired
    public APIUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public UsersDTO getAllUsers() {
        log.info("{}", userService.getAllUsers());
        return userService.getAllUsers();
    }
}
