package ua.testing.demo_jpa.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.testing.demo_jpa.auth.UserPrincipal;
import ua.testing.demo_jpa.dto.PageDTO;
import ua.testing.demo_jpa.dto.UserOrderDTO;
import ua.testing.demo_jpa.dto.UserProfileDTO;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.exceptions.ForbiddenPageException;
import ua.testing.demo_jpa.exceptions.UserNotFoundException;
import ua.testing.demo_jpa.service.OrderService;
import ua.testing.demo_jpa.service.UserService;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final String USERS_PAGE = "user_controller/users.html";
    private static final String USER_PAGE = "user_controller/user.html";
    private static final String FORBIDDEN_PAGE_EXCEPTION_MESSAGE = "Cannot access this page";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "Cannot find user with this email!";
    private final UserService userService;
    private final OrderService orderService;


    @GetMapping()
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String getUsersPage(Model model,
                               @PageableDefault(sort = {"id"}, size = 2) Pageable pageable,
                               Authentication authentication) {
        Page<User> userPage = userService.getAllUsers(pageable);
        Pageable currentPageable = userPage.getPageable();

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("page", getUsersPageDTO(userPage));
        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    model.addAttribute("userId", user.getUserId());
                });

        return USERS_PAGE;
    }

    @GetMapping("/{id}")
    public String getUserProfilePage(@PathVariable Long id,
                                     Model model,
                                     Authentication authentication,
                                     @PageableDefault(sort = {"id"}, size = 2) Pageable pageable) {
        Optional.ofNullable(authentication)
                .ifPresent(auth -> {
                    UserPrincipal user = (UserPrincipal) auth.getPrincipal();
                    if (!user.getUserId().equals(id)) {
                        throw new ForbiddenPageException(FORBIDDEN_PAGE_EXCEPTION_MESSAGE);
                    }
                    UserProfileDTO userDTO = userService.findUserByEmail(user.getUsername())
                            .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE));
                    model.addAttribute("user", userDTO);
                });

        Page<UserOrderDTO> orderPage = orderService.getAllUserOrders(pageable, id);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("page", getOrdersPageDTO(orderPage, orderPage.getPageable(), id));

        return USER_PAGE;
    }

    private PageDTO getOrdersPageDTO(Page<UserOrderDTO> orderPage, Pageable currentPageable, Long userId) {
        return PageDTO.builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(orderPage.getTotalPages())
                .hasPrev(orderPage.hasPrevious())
                .hasNext(orderPage.hasNext())
                .url("/users/" + userId)
                .build();
    }

    private PageDTO getUsersPageDTO(Page<User> userPage) {
        Pageable currentPageable = userPage.getPageable();
        return PageDTO.builder()
                .limit(currentPageable.getPageSize())
                .prevPage(currentPageable.getPageNumber() - 1)
                .nextPage(currentPageable.getPageNumber() + 1)
                .currentPage(currentPageable.getPageNumber() + 1)
                .totalPages(userPage.getTotalPages())
                .hasPrev(userPage.hasPrevious())
                .hasNext(userPage.hasNext())
                .url("/users/")
                .build();
    }
}
