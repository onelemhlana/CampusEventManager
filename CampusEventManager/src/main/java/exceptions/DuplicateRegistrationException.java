package exceptions;

/**
 * Custom checked exception - LU3 & LU4.
 */
public class DuplicateRegistrationException extends Exception {
    public DuplicateRegistrationException(String message) {
        super(message);
    }
}