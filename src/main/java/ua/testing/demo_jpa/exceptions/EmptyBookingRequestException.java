package ua.testing.demo_jpa.exceptions;

public class EmptyBookingRequestException extends RuntimeException {
    public EmptyBookingRequestException(String message) {
        super(message);
    }
}
