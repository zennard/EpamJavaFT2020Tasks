package ua.testing.demo_jpa.exceptions;

public class DescriptionNotFoundException extends RuntimeException {
    public DescriptionNotFoundException(String message) {
        super(message);
    }
}
