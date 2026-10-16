package io;

import model.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class FileHandler {
    private static final String DATA_DIR = "data/";
    private static final String STUDENTS_FILE = DATA_DIR + "students.txt";
    private static final String EVENTS_FILE = DATA_DIR + "events.txt";
    private static final String REGISTRATIONS_FILE = DATA_DIR + "registrations.csv";
    private static final String ORGANIZERS_FILE = DATA_DIR + "organizers.txt";

    public FileHandler() {
        // Ensure data directory exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ========== Students ==========
    public void saveStudents(Student[] students, int count) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (int i = 0; i < count; i++) {
                Student s = students[i];
                // id,name,email,course,year
                bw.write(s.getId() + "," + s.getName() + "," + s.getEmail() + ","
                        + s.getCourse() + "," + s.getYearOfStudy());
                bw.newLine();
            }
        }
    }

    public ArrayList<Student> loadStudents() throws IOException {
        ArrayList<Student> list = new ArrayList<>();
        File f = new File(STUDENTS_FILE);
        if (!f.exists()) {
            return list; // empty - caller can create sample data
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    try {
                        int year = Integer.parseInt(parts[4].trim());
                        Student s = new Student(parts[0].trim(), parts[1].trim(),
                                parts[2].trim(), parts[3].trim(), year);
                        list.add(s);
                    } catch (NumberFormatException nfe) {
                        // skip bad line
                    }
                }
            }
        }
        return list;
    }

    // ========== Events ==========
    public void saveEvents(Event[] events, int count) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENTS_FILE))) {
            for (int i = 0; i < count; i++) {
                Event e = events[i];
                // eventId,name,date,venue,capacity
                bw.write(e.getEventId() + "," + e.getName() + "," + e.getDate() + ","
                        + e.getVenue() + "," + e.getCapacity());
                bw.newLine();
            }
        }
    }

    public ArrayList<Event> loadEvents() throws IOException {
        ArrayList<Event> list = new ArrayList<>();
        File f = new File(EVENTS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    try {
                        int cap = Integer.parseInt(parts[4].trim());
                        Event e = new Event(parts[0].trim(), parts[1].trim(),
                                parts[2].trim(), parts[3].trim(), cap);
                        list.add(e);
                    } catch (NumberFormatException nfe) {
                        // skip
                    }
                }
            }
        }
        return list;
    }

    // ========== Registrations (CSV) ==========
    public void saveRegistrations(Event[] events, int eventCount) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REGISTRATIONS_FILE))) {
            bw.write("eventId,studentId");
            bw.newLine();
            for (int i = 0; i < eventCount; i++) {
                Event e = events[i];
                Student[] regs = e.getRegisteredStudents();
                for (int j = 0; j < e.getRegisteredCount(); j++) {
                    if (regs[j] != null) {
                        bw.write(e.getEventId() + "," + regs[j].getId());
                        bw.newLine();
                    }
                }
            }
        }
    }

    /**
     * Loads registrations. Requires that students and events are already loaded into DataManager.
     */
    public void loadRegistrations(DataManager dm) throws IOException {
        File f = new File(REGISTRATIONS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String eventId = parts[0].trim();
                    String studentId = parts[1].trim();
                    Event e = dm.findEventById(eventId);
                    Student s = dm.findStudentById(studentId);
                    if (e != null && s != null) {
                        try {
                            e.register(s); // may throw if capacity issues, but we ignore for load
                        } catch (Exception ignored) {
                            // On load we force-add if possible, or skip
                        }
                    }
                }
            }
        }
        dm.rebuildAttendanceMatrix();
    }

    // ========== Organizers ==========
    public void saveOrganizers(ArrayList<Organizer> organizers) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORGANIZERS_FILE))) {
            for (Organizer o : organizers) {
                // id,name,email,department,password
                bw.write(o.getId() + "," + o.getName() + "," + o.getEmail() + ","
                        + o.getDepartment() + "," + o.getPassword());
                bw.newLine();
            }
        }
    }

    public ArrayList<Organizer> loadOrganizers() throws IOException {
        ArrayList<Organizer> list = new ArrayList<>();
        File f = new File(ORGANIZERS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    Organizer o = new Organizer(parts[0].trim(), parts[1].trim(),
                            parts[2].trim(), parts[3].trim(), parts[4].trim());
                    list.add(o);
                }
            }
        }
        return list;
    }

    /**
     * Convenience method to load everything into a DataManager.
     * Handles missing files gracefully.
     */
    public void loadAll(DataManager dm) {
        try {
            // Organizers
            ArrayList<Organizer> orgs = loadOrganizers();
            for (Organizer o : orgs) dm.addOrganizer(o);

            // Students
            ArrayList<Student> studs = loadStudents();
            for (Student s : studs) dm.addStudent(s);

            // Events
            ArrayList<Event> evs = loadEvents();
            for (Event e : evs) dm.addEvent(e);

            // Registrations
            loadRegistrations(dm);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error loading data files: " + e.getMessage() +
                    "\nStarting with empty / sample data.",
                    "File Load Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void saveAll(DataManager dm) throws IOException {
        saveStudents(dm.getStudents(), dm.getStudentCount());
        saveEvents(dm.getEvents(), dm.getEventCount());
        saveRegistrations(dm.getEvents(), dm.getEventCount());
        saveOrganizers(dm.getOrganizers());
    }
}