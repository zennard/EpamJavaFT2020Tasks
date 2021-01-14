package ua.testing.demo_jpa.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ua.testing.demo_jpa.entity.User;
import ua.testing.demo_jpa.entity.UserPrincipal;
import ua.testing.demo_jpa.exceptions.EmailNotFoundException;
import ua.testing.demo_jpa.repository.UserRepository;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new EmailNotFoundException("user with email " + email + " was not found!"));
        return new UserPrincipal(user);
    }
}
