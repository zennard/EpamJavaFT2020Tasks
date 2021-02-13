package ua.testing.demo_jpa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.auth.RoleType;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.dto.UserProfileDTO;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.exceptions.IllegalEmailException;
import ua.testing.demo_jpa.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByUserLogin(UserLoginDTO userLoginDTO) {
        return userRepository.findByEmailAndPassword(
                userLoginDTO.getEmail(), encodePassword(userLoginDTO.getPassword()));
    }

    public Optional<UserProfileDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> UserProfileDTO.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .role(u.getRole())
                        .build());
    }

    public Optional<UserProfileDTO> findUserById(Long id) {
        return userRepository.findById(id)
                .map(u -> UserProfileDTO.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .role(u.getRole())
                        .build());
    }

    public void saveNewUser(UserRegistrationDTO userRegDTO) {
        try {
            User user = User.builder()
                    .firstName(userRegDTO.getFirstName())
                    .lastName(userRegDTO.getLastName())
                    .email(userRegDTO.getEmail())
                    .password(encodePassword(userRegDTO.getPassword()))
                    .role(RoleType.ROLE_USER)
                    .build();
            userRepository.save(user);
        } catch (Exception ex) {
            log.error("{This Email already exists}");
            throw new IllegalEmailException(ex);
        }

    }

    private static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
