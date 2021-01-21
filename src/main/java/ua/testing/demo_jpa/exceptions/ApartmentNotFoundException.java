package ua.testing.demo_jpa.exceptions;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException(String message) {
        super(message);
    }
}
