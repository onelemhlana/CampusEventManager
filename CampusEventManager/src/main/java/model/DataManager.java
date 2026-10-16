package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class DataManager {
    // Fixed-size arrays for demonstration (LU1)
    private Student[] students;
    private int studentCount;
    private static final int MAX_STUDENTS = 100;

    private Event[] events;
    private int eventCount;
    private static final int MAX_EVENTS = 50;

    // Dynamic lists
    private ArrayList<Organizer> organizers;
    private ArrayList<Person> allPeople; // polymorphism demo

    // 2D attendance matrix: rows = students, cols = events (boolean registered)
    private boolean[][] attendanceMatrix;

    public DataManager() {
        students = new Student[MAX_STUDENTS];
        studentCount = 0;
        events = new Event[MAX_EVENTS];
        eventCount = 0;
        organizers = new ArrayList<>();
        allPeople = new ArrayList<>();
        attendanceMatrix = new boolean[MAX_STUDENTS][MAX_EVENTS];
    }

    // ========== Student management ==========
    public void addStudent(Student s) {
        if (studentCount < MAX_STUDENTS) {
            students[studentCount++] = s;
            allPeople.add(s);
        }
    }

    public boolean removeStudent(String id) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getId().equalsIgnoreCase(id)) {
                // Shift left
                for (int j = i; j < studentCount - 1; j++) {
                    students[j] = students[j + 1];
                }
                students[--studentCount] = null;
                allPeople.removeIf(p -> p.getId().equalsIgnoreCase(id));
                return true;
            }
        }
        return false;
    }

    public Student findStudentById(String id) {
        // Linear search (LU1)
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getId().equalsIgnoreCase(id)) {
                return students[i];
            }
        }
        return null;
    }

    public Student[] getStudents() {
        return Arrays.copyOf(students, studentCount);
    }

    public int getStudentCount() { return studentCount; }

    public void sortStudentsByName() {
        // Using Arrays.sort with Comparator (LU1)
        Arrays.sort(students, 0, studentCount, Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER));
    }

    // ========== Event management ==========
    public void addEvent(Event e) {
        if (eventCount < MAX_EVENTS) {
            events[eventCount++] = e;
        }
    }

    public Event findEventById(String id) {
        for (int i = 0; i < eventCount; i++) {
            if (events[i].getEventId().equalsIgnoreCase(id)) {
                return events[i];
            }
        }
        return null;
    }

    public Event[] getEvents() {
        return Arrays.copyOf(events, eventCount);
    }

    public int getEventCount() { return eventCount; }

    public void sortEventsByName() {
        Arrays.sort(events, 0, eventCount, Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortEventsByDate() {
        Arrays.sort(events, 0, eventCount, Comparator.comparing(Event::getDate));
    }

    // ========== Organizer ==========
    public void addOrganizer(Organizer o) {
        organizers.add(o);
        allPeople.add(o);
    }

    public ArrayList<Organizer> getOrganizers() { return organizers; }

    public Organizer authenticate(String id, String password) {
        for (Organizer o : organizers) {
            if (o.getId().equals(id) && o.getPassword().equals(password)) {
                return o;
            }
        }
        return null;
    }

    // ========== Polymorphism demo ==========
    public ArrayList<Person> getAllPeople() {
        return allPeople;
    }

    // ========== Attendance Matrix (2D array) ==========
    public void updateAttendance(int studentIndex, int eventIndex, boolean registered) {
        if (studentIndex >= 0 && studentIndex < MAX_STUDENTS &&
            eventIndex >= 0 && eventIndex < MAX_EVENTS) {
            attendanceMatrix[studentIndex][eventIndex] = registered;
        }
    }

    public boolean[][] getAttendanceMatrix() {
        return attendanceMatrix;
    }

    public void rebuildAttendanceMatrix() {
        // Clear
        for (int i = 0; i < MAX_STUDENTS; i++) {
            Arrays.fill(attendanceMatrix[i], false);
        }
        // Rebuild from current registrations
        for (int e = 0; e < eventCount; e++) {
            Event ev = events[e];
            Student[] regs = ev.getRegisteredStudents();
            for (int s = 0; s < ev.getRegisteredCount(); s++) {
                if (regs[s] != null) {
                    int stuIdx = indexOfStudent(regs[s].getId());
                    if (stuIdx >= 0) {
                        attendanceMatrix[stuIdx][e] = true;
                    }
                }
            }
        }
    }

    private int indexOfStudent(String id) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getId().equals(id)) return i;
        }
        return -1;
    }

    // Utility to get a printable attendance report
    public String getAttendanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s", "Student ID"));
        for (int e = 0; e < eventCount; e++) {
            sb.append(String.format("%-10s", events[e].getEventId()));
        }
        sb.append("\n");
        for (int s = 0; s < studentCount; s++) {
            sb.append(String.format("%-12s", students[s].getId()));
            for (int e = 0; e < eventCount; e++) {
                sb.append(String.format("%-10s", attendanceMatrix[s][e] ? "YES" : "-"));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}