# Campus Event & Registration Manager

A complete Java Swing desktop application built with Apache NetBeans that demonstrates all six Learning Units (LU 1 – LU 6).

## Project Overview

This application allows a university to:
- Manage students and events
- Register students for events (with capacity control and waitlist)
- View attendance reports
- Save and load data from text/CSV files

## Login Credentials

- **ID:** `admin`
- **Password:** `admin123`

## Package Structure

src/
├── model/
│   ├── Person.java              (Abstract superclass)
│   ├── Student.java
│   ├── Organizer.java
│   ├── Event.java               (Implements Registrable)
│   ├── Registrable.java         (Interface)
│   └── DataManager.java         (Arrays, 2D matrix, sorting, searching)
├── exceptions/
│   ├── EventFullException.java
│   └── DuplicateRegistrationException.java
├── io/
│   └── FileHandler.java         (File I/O)
├── gui/
│   ├── LoginDialog.java
│   └── MainFrame.java           (Main Swing GUI)
└── main/
└── Main.java                (Entry point)
data/
├── students.txt
├── events.txt
├── registrations.csv
└── organizers.txt
text



## Learning Units Covered

| LU  | Concept Demonstrated                                      | Evidence in Project                                      |
|-----|-----------------------------------------------------------|----------------------------------------------------------|
| 1   | Arrays, 2D arrays, sorting & searching                    | `Student[]`, `Event[]`, `boolean[][]` attendance matrix, `Arrays.sort`, linear search, bubble sort |
| 2   | Inheritance basics                                        | `Student` and `Organizer` extend `Person`, use of `super()` |
| 3   | Abstract classes, interfaces, polymorphism, custom exceptions, method overloading | Abstract `Person`, `Registrable` interface, `ArrayList<Person>`, overloaded `register()` methods, custom exceptions |
| 4   | Exception handling (try-catch, checked & unchecked)       | Registration and file operations guarded with try-catch, user-friendly `JOptionPane` messages |
| 5   | File Input and Output                                     | Save/Load using `BufferedReader` / `BufferedWriter` for `.txt` and `.csv` files |
| 6   | Swing components & advanced GUI                           | `JTabbedPane`, `JTable`, `JMenuBar`, `JToolBar`, `JComboBox`, `JCheckBox`, `JProgressBar`, layouts, listeners |

## Features

- Login screen for organizers
- Student management (Add / Update / Delete / Sort)
- Event management with capacity control
- Student registration with waitlist and priority option
- Custom exceptions (`EventFullException`, `DuplicateRegistrationException`)
- Attendance matrix report (2D array)
- Polymorphism demonstration
- Save and Load all data
- Progress bar during file operations

## How to Run

1. Open the project in **NetBeans**.
2. Right-click the project → **Properties** → **Run** → set Main Class to `main.Main`.
3. Clean and Build the project.
4. Run the project (F6).
5. Login with `admin` / `admin123`.

## Sample Data

The `data` folder contains sample students, events, registrations, and the default organizer account.

## Author

Created as a demonstration project covering Advanced Array Concepts, Inheritance, Exception Handling, File I/O, and Swing GUI.


