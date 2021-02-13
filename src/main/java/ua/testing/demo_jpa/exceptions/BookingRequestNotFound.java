package ua.testing.demo_jpa.exceptions;

public class BookingRequestNotFound extends RuntimeException {
    public BookingRequestNotFound(String message) {
        super(message);
    }
}
