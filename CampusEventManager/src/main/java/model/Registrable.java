package model;

import exceptions.EventFullException;
import exceptions.DuplicateRegistrationException;

/**
 * Interface demonstrating LU3 advanced inheritance concepts.
 */
public interface Registrable {
    void register(Student s) throws EventFullException, DuplicateRegistrationException;
    void cancel(Student s);
}
