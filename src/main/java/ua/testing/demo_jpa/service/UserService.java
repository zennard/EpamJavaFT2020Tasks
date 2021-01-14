package ua.testing.demo_jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.entity.RoleType;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.exceptions.IllegalEmailException;
import ua.testing.demo_jpa.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        //TODO checking for an empty user list
        return userRepository.findAll(pageable);
    }

    public Optional<User> findByUserLogin(UserLoginDTO userLoginDTO) {
        return userRepository.findByEmailAndPassword(
                userLoginDTO.getEmail(), encodePassword(userLoginDTO.getPassword()));
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
            log.info("{This Email already exists}");
            throw new IllegalEmailException(ex);
        }

    }

    private static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
