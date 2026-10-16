package main;

import model.*;
import io.FileHandler;
import gui.LoginDialog;
import gui.MainFrame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            DataManager dm = new DataManager();
            FileHandler fh = new FileHandler();

            // Try to load existing data; if empty, create sample data
            fh.loadAll(dm);

            if (dm.getStudentCount() == 0 && dm.getEventCount() == 0) {
                createSampleData(dm);
                try {
                    fh.saveAll(dm);
                } catch (Exception e) {
                    // ignore on first run
                }
            }

            // Ensure at least one organizer exists
            if (dm.getOrganizers().isEmpty()) {
                Organizer admin = new Organizer("admin", "System Admin", "admin@campus.edu",
                        "IT Services", "admin123");
                dm.addOrganizer(admin);
                try {
                    fh.saveOrganizers(dm.getOrganizers());
                } catch (Exception ignored) {}
            }

            // Show login
            LoginDialog login = new LoginDialog(null, dm);
            login.setVisible(true);

            if (login.isAuthenticated()) {
                MainFrame frame = new MainFrame(dm, fh, login.getLoggedInOrganizer());
                frame.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }

    private static void createSampleData(DataManager dm) {
        // Students
        dm.addStudent(new Student("S001", "Thabo Molefe", "thabo@student.ac.za", "Computer Science", 2));
        dm.addStudent(new Student("S002", "Aisha Naidoo", "aisha@student.ac.za", "Information Systems", 3));
        dm.addStudent(new Student("S003", "Johan van der Berg", "johan@student.ac.za", "Software Engineering", 1));
        dm.addStudent(new Student("S004", "Lerato Dlamini", "lerato@student.ac.za", "Computer Science", 2));
        dm.addStudent(new Student("S005", "Priya Patel", "priya@student.ac.za", "Data Science", 3));

        // Events
        Event e1 = new Event("E001", "Java Coding Workshop", "2025-03-15", "Lab 3A", 3);
        Event e2 = new Event("E002", "Career Fair 2025", "2025-04-10", "Main Hall", 50);
        Event e3 = new Event("E003", "AI Guest Lecture", "2025-03-22", "Auditorium B", 5);

        dm.addEvent(e1);
        dm.addEvent(e2);
        dm.addEvent(e3);

        // Some registrations (will demonstrate capacity)
        try {
            e1.register(dm.findStudentById("S001"));
            e1.register(dm.findStudentById("S002"));
            e1.register(dm.findStudentById("S003"));
            // Next one should go to waitlist / throw EventFullException if not caught
        } catch (Exception ignored) {}

        try {
            e3.register(dm.findStudentById("S004"));
            e3.register(dm.findStudentById("S005"));
        } catch (Exception ignored) {}

        dm.rebuildAttendanceMatrix();
    }
}