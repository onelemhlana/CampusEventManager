package gui;

import model.DataManager;
import model.Student;
import model.Event;
import model.Organizer;
import model.Person;
import io.FileHandler;
import exceptions.EventFullException;
import exceptions.DuplicateRegistrationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;


public class MainFrame extends JFrame {
    private DataManager dataManager;
    private FileHandler fileHandler;
    private Organizer currentUser;

    // Tables & models
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private DefaultTableModel eventTableModel;
    private JTable eventTable;
    private DefaultTableModel regTableModel;
    private JTable regTable;

    // Form fields - Students
    private JTextField txtStuId, txtStuName, txtStuEmail, txtStuCourse, txtStuYear;

    // Form fields - Events
    private JTextField txtEvId, txtEvName, txtEvDate, txtEvVenue, txtEvCapacity;

    // Registration
    private JComboBox<String> cmbStudents, cmbEvents;
    private JCheckBox chkPriority;

    // Reports
    private JTextArea txtReport;
    private JProgressBar progressBar;

    public MainFrame(DataManager dm, FileHandler fh, Organizer user) {
        this.dataManager = dm;
        this.fileHandler = fh;
        this.currentUser = user;

        setTitle("Campus Event & Registration Manager - Logged in as: " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        // Menu bar
        createMenuBar();

        // Toolbar
        createToolBar();

        // Main tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", createStudentsPanel());
        tabs.addTab("Events", createEventsPanel());
        tabs.addTab("Registrations", createRegistrationsPanel());
        tabs.addTab("Reports", createReportsPanel());

        add(tabs, BorderLayout.CENTER);

        // Status / progress
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        add(progressBar, BorderLayout.SOUTH);

        // Initial load of tables
        refreshStudentTable();
        refreshEventTable();
        refreshRegistrationComboBoxes();
    }

    // ==================== Menu ====================
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem miSave = new JMenuItem("Save All Data");
        JMenuItem miLoad = new JMenuItem("Load All Data");
        JMenuItem miExit = new JMenuItem("Exit");

        miSave.addActionListener(e -> saveAllData());
        miLoad.addActionListener(e -> loadAllData());
        miExit.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(this, "Exit application?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) System.exit(0);
        });

        fileMenu.add(miSave);
        fileMenu.add(miLoad);
        fileMenu.addSeparator();
        fileMenu.add(miExit);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(e -> showAbout());
        helpMenu.add(miAbout);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnSave = new JButton("Save");
        btnSave.setToolTipText("Save all data to files");
        btnSave.addActionListener(e -> saveAllData());

        JButton btnLoad = new JButton("Load");
        btnLoad.setToolTipText("Load data from files");
        btnLoad.addActionListener(e -> loadAllData());

        toolBar.add(btnSave);
        toolBar.add(btnLoad);
        toolBar.addSeparator();

        add(toolBar, BorderLayout.NORTH);
    }

    // ==================== Students Tab ====================
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Table
        String[] cols = {"ID", "Name", "Email", "Course", "Year"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Sorting / filtering support (LU6 advanced)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(studentTable);
        panel.add(scroll, BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStuId = new JTextField(12);
        txtStuName = new JTextField(18);
        txtStuEmail = new JTextField(18);
        txtStuCourse = new JTextField(14);
        txtStuYear = new JTextField(5);

        int row = 0;
        addFormRow(form, gbc, row++, "ID:", txtStuId);
        addFormRow(form, gbc, row++, "Name:", txtStuName);
        addFormRow(form, gbc, row++, "Email:", txtStuEmail);
        addFormRow(form, gbc, row++, "Course:", txtStuCourse);
        addFormRow(form, gbc, row++, "Year:", txtStuYear);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Student");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnSort = new JButton("Sort by Name");

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearStudentForm());
        btnSort.addActionListener(e -> {
            dataManager.sortStudentsByName();
            refreshStudentTable();
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnSort);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        // Populate form on selection
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = studentTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = studentTable.convertRowIndexToModel(viewRow);
                    txtStuId.setText((String) studentTableModel.getValueAt(modelRow, 0));
                    txtStuName.setText((String) studentTableModel.getValueAt(modelRow, 1));
                    txtStuEmail.setText((String) studentTableModel.getValueAt(modelRow, 2));
                    txtStuCourse.setText((String) studentTableModel.getValueAt(modelRow, 3));
                    txtStuYear.setText(String.valueOf(studentTableModel.getValueAt(modelRow, 4)));
                }
            }
        });

        panel.add(form, BorderLayout.EAST);
        return panel;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private void addStudent() {
        try {
            String id = txtStuId.getText().trim();
            String name = txtStuName.getText().trim();
            String email = txtStuEmail.getText().trim();
            String course = txtStuCourse.getText().trim();
            int year = Integer.parseInt(txtStuYear.getText().trim());

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dataManager.findStudentById(id) != null) {
                JOptionPane.showMessageDialog(this, "Student ID already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Student s = new Student(id, name, email, course, year);
            dataManager.addStudent(s);
            refreshStudentTable();
            refreshRegistrationComboBoxes();
            clearStudentForm();
            JOptionPane.showMessageDialog(this, "Student added successfully.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Year must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        String id = txtStuId.getText().trim();
        Student existing = dataManager.findStudentById(id);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Select a student first or check ID.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            existing.setName(txtStuName.getText().trim());
            existing.setEmail(txtStuEmail.getText().trim());
            existing.setCourse(txtStuCourse.getText().trim());
            existing.setYearOfStudy(Integer.parseInt(txtStuYear.getText().trim()));
            refreshStudentTable();
            JOptionPane.showMessageDialog(this, "Student updated.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Year must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int viewRow = studentTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Delete selected student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        int modelRow = studentTable.convertRowIndexToModel(viewRow);
        String id = (String) studentTableModel.getValueAt(modelRow, 0);

        if (dataManager.removeStudent(id)) {
            refreshStudentTable();
            refreshRegistrationComboBoxes();
            clearStudentForm();
            JOptionPane.showMessageDialog(this, "Student deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearStudentForm() {
        txtStuId.setText("");
        txtStuName.setText("");
        txtStuEmail.setText("");
        txtStuCourse.setText("");
        txtStuYear.setText("");
        studentTable.clearSelection();
    }

    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        for (Student s : dataManager.getStudents()) {
            studentTableModel.addRow(new Object[]{
                    s.getId(), s.getName(), s.getEmail(), s.getCourse(), s.getYearOfStudy()
            });
        }
    }

    // ==================== Events Tab ====================
    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"ID", "Name", "Date", "Venue", "Capacity", "Registered"};
        eventTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.setRowSorter(new TableRowSorter<>(eventTableModel));

        panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Event Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtEvId = new JTextField(10);
        txtEvName = new JTextField(18);
        txtEvDate = new JTextField(12);
        txtEvVenue = new JTextField(14);
        txtEvCapacity = new JTextField(5);

        int row = 0;
        addFormRow(form, gbc, row++, "Event ID:", txtEvId);
        addFormRow(form, gbc, row++, "Name:", txtEvName);
        addFormRow(form, gbc, row++, "Date (YYYY-MM-DD):", txtEvDate);
        addFormRow(form, gbc, row++, "Venue:", txtEvVenue);
        addFormRow(form, gbc, row++, "Capacity:", txtEvCapacity);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Event");
        JButton btnClear = new JButton("Clear");
        JButton btnSortName = new JButton("Sort by Name");
        JButton btnSortDate = new JButton("Sort by Date");

        btnAdd.addActionListener(e -> addEvent());
        btnClear.addActionListener(e -> clearEventForm());
        btnSortName.addActionListener(e -> {
            dataManager.sortEventsByName();
            refreshEventTable();
        });
        btnSortDate.addActionListener(e -> {
            dataManager.sortEventsByDate();
            refreshEventTable();
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);
        btnPanel.add(btnSortName);
        btnPanel.add(btnSortDate);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = eventTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = eventTable.convertRowIndexToModel(viewRow);
                    txtEvId.setText((String) eventTableModel.getValueAt(modelRow, 0));
                    txtEvName.setText((String) eventTableModel.getValueAt(modelRow, 1));
                    txtEvDate.setText((String) eventTableModel.getValueAt(modelRow, 2));
                    txtEvVenue.setText((String) eventTableModel.getValueAt(modelRow, 3));
                    txtEvCapacity.setText(String.valueOf(eventTableModel.getValueAt(modelRow, 4)));
                }
            }
        });

        panel.add(form, BorderLayout.EAST);
        return panel;
    }

    private void addEvent() {
        try {
            String id = txtEvId.getText().trim();
            String name = txtEvName.getText().trim();
            String date = txtEvDate.getText().trim();
            String venue = txtEvVenue.getText().trim();
            int cap = Integer.parseInt(txtEvCapacity.getText().trim());

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name are required.");
                return;
            }
            if (dataManager.findEventById(id) != null) {
                JOptionPane.showMessageDialog(this, "Event ID already exists.");
                return;
            }
            Event e = new Event(id, name, date, venue, cap);
            dataManager.addEvent(e);
            refreshEventTable();
            refreshRegistrationComboBoxes();
            clearEventForm();
            JOptionPane.showMessageDialog(this, "Event added.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearEventForm() {
        txtEvId.setText("");
        txtEvName.setText("");
        txtEvDate.setText("");
        txtEvVenue.setText("");
        txtEvCapacity.setText("");
        eventTable.clearSelection();
    }

    private void refreshEventTable() {
        eventTableModel.setRowCount(0);
        for (Event e : dataManager.getEvents()) {
            eventTableModel.addRow(new Object[]{
                    e.getEventId(), e.getName(), e.getDate(), e.getVenue(),
                    e.getCapacity(), e.getRegisteredCount()
            });
        }
    }

    // ==================== Registrations Tab ====================
    private JPanel createRegistrationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        form.setBorder(BorderFactory.createTitledBorder("Register Student to Event"));

        cmbStudents = new JComboBox<>();
        cmbEvents = new JComboBox<>();
        chkPriority = new JCheckBox("Priority (front of waitlist)");

        form.add(new JLabel("Student:"));
        form.add(cmbStudents);
        form.add(new JLabel("Event:"));
        form.add(cmbEvents);
        form.add(chkPriority);

        JButton btnRegister = new JButton("Register");
        JButton btnCancelReg = new JButton("Cancel Registration");
        btnRegister.addActionListener(e -> registerStudent());
        btnCancelReg.addActionListener(e -> cancelRegistration());

        form.add(btnRegister);
        form.add(btnCancelReg);

        panel.add(form, BorderLayout.NORTH);

        // Table of current registrations
        String[] cols = {"Event ID", "Event Name", "Student ID", "Student Name", "Role"};
        regTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        regTable = new JTable(regTableModel);
        panel.add(new JScrollPane(regTable), BorderLayout.CENTER);

        // Polymorphism demo button
        JButton btnShowRoles = new JButton("Show All People Roles (Polymorphism Demo)");
        btnShowRoles.addActionListener(e -> showPolymorphismDemo());
        panel.add(btnShowRoles, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshRegistrationComboBoxes() {
        cmbStudents.removeAllItems();
        for (Student s : dataManager.getStudents()) {
            cmbStudents.addItem(s.getId() + " - " + s.getName());
        }
        cmbEvents.removeAllItems();
        for (Event e : dataManager.getEvents()) {
            cmbEvents.addItem(e.getEventId() + " - " + e.getName());
        }
        refreshRegTable();
    }

    private void registerStudent() {
        if (cmbStudents.getSelectedItem() == null || cmbEvents.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select both student and event.");
            return;
        }
        try {
            String stuStr = (String) cmbStudents.getSelectedItem();
            String evStr = (String) cmbEvents.getSelectedItem();
            String stuId = stuStr.split(" - ")[0];
            String evId = evStr.split(" - ")[0];

            Student s = dataManager.findStudentById(stuId);
            Event e = dataManager.findEventById(evId);

            if (s == null || e == null) {
                JOptionPane.showMessageDialog(this, "Could not find student or event.");
                return;
            }

            boolean priority = chkPriority.isSelected();
            e.register(s, priority); // may throw

            dataManager.rebuildAttendanceMatrix();
            refreshEventTable();
            refreshRegTable();
            JOptionPane.showMessageDialog(this, "Registration successful!\n" + s.getRoleDetails());
        } catch (EventFullException | DuplicateRegistrationException ex) {
            // Custom exceptions caught and shown nicely (LU4)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Issue", JOptionPane.WARNING_MESSAGE);
            refreshEventTable();
            refreshRegTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelRegistration() {
        int row = regTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a registration row first.");
            return;
        }
        String evId = (String) regTableModel.getValueAt(row, 0);
        String stuId = (String) regTableModel.getValueAt(row, 2);

        Event e = dataManager.findEventById(evId);
        Student s = dataManager.findStudentById(stuId);
        if (e != null && s != null) {
            e.cancel(s);
            dataManager.rebuildAttendanceMatrix();
            refreshEventTable();
            refreshRegTable();
            JOptionPane.showMessageDialog(this, "Registration cancelled.");
        }
    }

    private void refreshRegTable() {
        regTableModel.setRowCount(0);
        for (Event e : dataManager.getEvents()) {
            Student[] regs = e.getRegisteredStudents();
            for (int i = 0; i < e.getRegisteredCount(); i++) {
                if (regs[i] != null) {
                    regTableModel.addRow(new Object[]{
                            e.getEventId(), e.getName(),
                            regs[i].getId(), regs[i].getName(),
                            regs[i].getRole()   // polymorphism
                    });
                }
            }
        }
    }

    private void showPolymorphismDemo() {
        StringBuilder sb = new StringBuilder("=== Polymorphism Demo (Person reference) ===\n\n");
        for (Person p : dataManager.getAllPeople()) {
            // Call overridden methods via superclass reference
            sb.append(p.getId()).append(" -> ").append(p.getRole())
              .append(" | ").append(p.getRoleDetails()).append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Polymorphism - getRole() / getRoleDetails()", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== Reports Tab ====================
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        txtReport = new JTextArea();
        txtReport.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtReport.setEditable(false);
        panel.add(new JScrollPane(txtReport), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAttendance = new JButton("Show Attendance Matrix (2D Array)");
        JButton btnSortedEvents = new JButton("Show Events Sorted by Name");
        JButton btnPeople = new JButton("List All People (Polymorphism)");

        btnAttendance.addActionListener(e -> {
            dataManager.rebuildAttendanceMatrix();
            txtReport.setText("=== ATTENDANCE MATRIX (Students x Events) ===\n\n" +
                    dataManager.getAttendanceReport());
        });
        btnSortedEvents.addActionListener(e -> {
            dataManager.sortEventsByName();
            StringBuilder sb = new StringBuilder("=== EVENTS SORTED BY NAME ===\n\n");
            for (Event ev : dataManager.getEvents()) {
                sb.append(ev.toString()).append("\n");
            }
            txtReport.setText(sb.toString());
            refreshEventTable();
        });
        btnPeople.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("=== ALL PEOPLE (Polymorphic list) ===\n\n");
            for (Person p : dataManager.getAllPeople()) {
                sb.append(p.toString()).append("  ->  ").append(p.getRoleDetails()).append("\n");
            }
            txtReport.setText(sb.toString());
        });

        btnPanel.add(btnAttendance);
        btnPanel.add(btnSortedEvents);
        btnPanel.add(btnPeople);
        panel.add(btnPanel, BorderLayout.NORTH);

        return panel;
    }

    // ==================== File operations ====================
    private void saveAllData() {
        progressBar.setIndeterminate(true);
        progressBar.setString("Saving...");
        try {
            fileHandler.saveAll(dataManager);
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            progressBar.setString("Saved successfully");
            JOptionPane.showMessageDialog(this, "All data saved to data/ folder.");
        } catch (IOException ex) {
            progressBar.setIndeterminate(false);
            progressBar.setString("Save failed");
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllData() {
        int conf = JOptionPane.showConfirmDialog(this,
                "Load data from files? Current in-memory data will be replaced.",
                "Confirm Load", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        progressBar.setIndeterminate(true);
        progressBar.setString("Loading...");
        try {
            // Create fresh manager and reload
            DataManager newDm = new DataManager();
            fileHandler.loadAll(newDm);
            this.dataManager = newDm;

            refreshStudentTable();
            refreshEventTable();
            refreshRegistrationComboBoxes();

            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            progressBar.setString("Loaded successfully");
            JOptionPane.showMessageDialog(this, "Data loaded from files.");
        } catch (Exception ex) {
            progressBar.setIndeterminate(false);
            progressBar.setString("Load failed");
            JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Campus Event & Registration Manager\n" +
                "Version 1.0\n\n" +
                "Demonstrates:\n" +
                "LU1 - Arrays, 2D arrays, sorting/searching\n" +
                "LU2 - Inheritance (Person -> Student/Organizer)\n" +
                "LU3 - Abstract class, interface, polymorphism, custom exceptions\n" +
                "LU4 - Exception handling (try-catch, custom + standard)\n" +
                "LU5 - File I/O (txt / csv)\n" +
                "LU6 - Swing GUI (tabs, tables, menus, dialogs)\n\n" +
                "Built for NetBeans / Java SE",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}