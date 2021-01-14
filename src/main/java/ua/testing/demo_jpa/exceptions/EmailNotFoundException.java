package ua.testing.demo_jpa.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class EmailNotFoundException extends UsernameNotFoundException {
    public EmailNotFoundException(String msg) {
        super(msg);
    }
}
