package exceptions;

/**
 * Custom checked exception - LU3 & LU4.
 */
public class EventFullException extends Exception {
    public EventFullException(String message) {
        super(message);
    }
}