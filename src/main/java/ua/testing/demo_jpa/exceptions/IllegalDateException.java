package ua.testing.demo_jpa.exceptions;

public class IllegalDateException extends IllegalArgumentException {
    public IllegalDateException(String message) {
        super(message);
    }
}
