package ua.testing.demo_jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.dto.UserLoginDTO;
import ua.testing.demo_jpa.dto.UserRegistrationDTO;
import ua.testing.demo_jpa.dto.UsersDTO;
import ua.testing.demo_jpa.entity.RoleType;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.exceptions.IllegalEmailException;
import ua.testing.demo_jpa.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UsersDTO getAllUsers() {
        //TODO checking for an empty user list
        return new UsersDTO(userRepository.findAll());
    }

    public Optional<User> findByUserLogin(UserLoginDTO userLoginDTO) {
        //TODO check for user availability. password check
        return userRepository.findByEmailAndPassword(
                userLoginDTO.getEmail(), getPasswordHash(userLoginDTO.getPassword()));
    }

    public void saveNewUser(UserRegistrationDTO userRegDTO) {
        try {
            User user = User.builder()
                    .firstName(userRegDTO.getFirstName())
                    .lastName(userRegDTO.getLastName())
                    .email(userRegDTO.getEmail())
                    .password(getPasswordHash(userRegDTO.getPassword()))
                    .role(RoleType.ROLE_USER)
                    .build();
            userRepository.save(user);
        } catch (Exception ex) {
            log.info("{Почтовый адрес уже существует}");
            throw new IllegalEmailException(ex);
        }

    }

    private static String getPasswordHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            log.error("Cannot find a digest method in 'getPasswordHash()'", e);
        }
        byte[] encodedhash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
