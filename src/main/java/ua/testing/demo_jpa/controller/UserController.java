package ua.testing.demo_jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.dto.PageDTO;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.service.UserService;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {
    private static final String USERS_PAGE = "user_controller/users.html";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getUsersPage(Model model,
                               @PageableDefault(sort = {"id"}, size = 2) Pageable pageable) {
        Page<User> userPage = userService.getAllUsers(pageable);
        Pageable currentPageable = userPage.getPageable();

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("page", PageDTO
                .builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(userPage.getTotalPages())
                .hasPrev(userPage.hasPrevious())
                .hasNext(userPage.hasNext())
                .url("/users/")
                .build());

        return USERS_PAGE;
    }
}
