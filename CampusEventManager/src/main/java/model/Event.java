package model;

import exceptions.EventFullException;
import exceptions.DuplicateRegistrationException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Event class demonstrating arrays, ArrayList, capacity management,
 * and throwing custom exceptions (LU1, LU3, LU4).
 */
public class Event implements Registrable {
    private String eventId;
    private String name;
    private String date;          // simple String for demo (YYYY-MM-DD)
    private String venue;
    private int capacity;
    private Student[] registeredStudents; // fixed-size array (LU1)
    private int registeredCount;
    private ArrayList<Student> waitlist;  // dynamic ArrayList (LU1 comparison)

    public Event(String eventId, String name, String date, String venue, int capacity) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.capacity = capacity;
        this.registeredStudents = new Student[capacity]; // fixed array
        this.registeredCount = 0;
        this.waitlist = new ArrayList<>();
    }

    // Getters
    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public int getRegisteredCount() { return registeredCount; }
    public Student[] getRegisteredStudents() { return registeredStudents; }
    public ArrayList<Student> getWaitlist() { return waitlist; }

    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setVenue(String venue) { this.venue = venue; }

    /**
     * Register a student – basic overload (LU3 method overloading).
     */
    @Override
    public void register(Student s) throws EventFullException, DuplicateRegistrationException {
        register(s, false);
    }

    /**
     * Overloaded register method with priority flag (LU3 method overloading).
     */
    public void register(Student s, boolean priority) throws EventFullException, DuplicateRegistrationException {
        // Check for duplicate
        for (int i = 0; i < registeredCount; i++) {
            if (registeredStudents[i] != null && registeredStudents[i].getId().equals(s.getId())) {
                throw new DuplicateRegistrationException("Student " + s.getId() + " already registered for " + name);
            }
        }
        // Also check waitlist
        for (Student w : waitlist) {
            if (w.getId().equals(s.getId())) {
                throw new DuplicateRegistrationException("Student already on waitlist for " + name);
            }
        }

        if (registeredCount < capacity) {
            registeredStudents[registeredCount++] = s;
        } else {
            if (priority) {
                // Simple priority: insert at front of waitlist
                waitlist.add(0, s);
            } else {
                waitlist.add(s);
            }
            throw new EventFullException("Event '" + name + "' is full (capacity " + capacity + "). Student added to waitlist.");
        }
    }

    @Override
    public void cancel(Student s) {
        // Remove from registered array
        for (int i = 0; i < registeredCount; i++) {
            if (registeredStudents[i] != null && registeredStudents[i].getId().equals(s.getId())) {
                // Shift left
                for (int j = i; j < registeredCount - 1; j++) {
                    registeredStudents[j] = registeredStudents[j + 1];
                }
                registeredStudents[registeredCount - 1] = null;
                registeredCount--;
                // Promote from waitlist if available
                if (!waitlist.isEmpty()) {
                    Student next = waitlist.remove(0);
                    registeredStudents[registeredCount++] = next;
                }
                return;
            }
        }
        // Remove from waitlist
        waitlist.removeIf(w -> w.getId().equals(s.getId()));
    }

    public boolean isRegistered(Student s) {
        for (int i = 0; i < registeredCount; i++) {
            if (registeredStudents[i] != null && registeredStudents[i].getId().equals(s.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return eventId + " | " + name + " | " + date + " | " + venue + " | Cap: " + capacity + " | Reg: " + registeredCount;
    }

    /**
     * Helper to get a sorted copy of registered students by name (LU1 sorting).
     */
    public Student[] getSortedRegisteredByName() {
        Student[] copy = Arrays.copyOf(registeredStudents, registeredCount);
        // Bubble sort for demonstration (LU1)
        for (int i = 0; i < copy.length - 1; i++) {
            for (int j = 0; j < copy.length - i - 1; j++) {
                if (copy[j].getName().compareToIgnoreCase(copy[j + 1].getName()) > 0) {
                    Student temp = copy[j];
                    copy[j] = copy[j + 1];
                    copy[j + 1] = temp;
                }
            }
        }
        return copy;
    }
}