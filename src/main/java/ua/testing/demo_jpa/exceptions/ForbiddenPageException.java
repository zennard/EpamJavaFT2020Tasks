package ua.testing.demo_jpa.exceptions;

public class ForbiddenPageException extends RuntimeException {
    public ForbiddenPageException(String message) {
        super(message);
    }
}
