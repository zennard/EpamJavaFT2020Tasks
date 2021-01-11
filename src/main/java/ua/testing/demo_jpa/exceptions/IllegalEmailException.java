package ua.testing.demo_jpa.exceptions;

public class IllegalEmailException extends RuntimeException {
    public IllegalEmailException(Throwable cause) {
        super(cause);
    }
}
