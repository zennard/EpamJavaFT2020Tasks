package ua.testing.demo_jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ua.testing.demo_jpa.auth.RoleType;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.dto.UserProfileDTO;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService testedInstance;

    @Test
    void shouldGetAllUsers() {
        //given
        int expectedNumber = 2;
        when(userRepository.findAll(PageRequest.of(0, 2)))
                .thenReturn(givenUsersPage());

        //when
        Page<User> usersPage = testedInstance.getAllUsers(PageRequest.of(0, 2));
        //then
        assertThat(usersPage.getContent().size()).isEqualTo(expectedNumber);

        User user = usersPage.getContent().get(0);
        assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("firstName", "Tom")
                .hasFieldOrPropertyWithValue("lastName", "Hank")
                .hasFieldOrPropertyWithValue("role", RoleType.ROLE_USER)
                .hasFieldOrPropertyWithValue("email", "r@r.com");
    }

    Page<User> givenUsersPage() {
        return new PageImpl<>(
                Arrays.asList(
                        User.builder()
                                .id(1L)
                                .role(RoleType.ROLE_USER)
                                .firstName("Tom")
                                .lastName("Hank")
                                .email("r@r.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .role(RoleType.ROLE_MANAGER)
                                .firstName("Bob")
                                .lastName("Rust")
                                .email("g@g.com")
                                .build()
                ),
                PageRequest.of(0, 2),
                1);
    }

    @Test
    void shouldReturnUserWithId1() {
        //given 
        when(userRepository.findById(any()))
                .thenReturn(givenUser());

        //when
        UserProfileDTO user = testedInstance.findUserById(1L).orElseGet(UserProfileDTO::new);
        //then
        assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("firstName", "Alex")
                .hasFieldOrPropertyWithValue("lastName", "Kere")
                .hasFieldOrPropertyWithValue("role", RoleType.ROLE_USER)
                .hasFieldOrPropertyWithValue("email", "a@a.com");
    }

    private Optional<User> givenUser() {
        return Optional.of(User.builder()
                .id(1L)
                .email("a@a.com")
                .role(RoleType.ROLE_USER)
                .firstName("Alex")
                .lastName("Kere")
                .build());
    }

    @Test
    void shouldReturnUserWithCorrectEmailAndPassword() {
        //given
        when(userRepository.findByEmailAndPassword(any(), any()))
                .thenReturn(givenUser());
        //when
        Optional<User> user = testedInstance.findByUserLogin(
                UserLoginDTO.builder()
                        .email("a@a.com")
                        .password("password")
                        .build()
        );
        //then
        assertThat(user.get())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "a@a.com");
    }
}