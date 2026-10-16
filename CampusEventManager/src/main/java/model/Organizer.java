package model;

public class Organizer extends Person {
    private String department;
    private String password; // simple for login demo

    public Organizer(String id, String name, String email, String department, String password) {
        super(id, name, email);
        this.department = department;
        this.password = password;
    }

    public String getDepartment() { return department; }
    public String getPassword() { return password; }

    public void setDepartment(String department) { this.department = department; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String getRole() {
        return "Organizer";
    }

    @Override
    public String getRoleDetails() {
        return "Organizer - Department: " + department;
    }

    @Override
    public String toString() {
        return super.toString() + " | Dept: " + department;
    }
}