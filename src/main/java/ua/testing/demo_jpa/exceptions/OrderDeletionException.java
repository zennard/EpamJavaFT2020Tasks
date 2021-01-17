package ua.testing.demo_jpa.exceptions;

public class OrderDeletionException extends IllegalArgumentException {
    public OrderDeletionException(String s) {
        super(s);
    }
}
