package ua.testing.demo_jpa.exceptions;

public class WrongTimetableIdException extends IllegalArgumentException {
    public WrongTimetableIdException(String s) {
        super(s);
    }
}
