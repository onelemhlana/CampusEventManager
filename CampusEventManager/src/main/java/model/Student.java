package model;


public class Student extends Person {
    private String course;
    private int yearOfStudy;

    public Student(String id, String name, String email, String course, int yearOfStudy) {
        super(id, name, email);          // LU2 - calling superclass constructor
        this.course = course;
        this.yearOfStudy = yearOfStudy;
    }

    public String getCourse() { return course; }
    public int getYearOfStudy() { return yearOfStudy; }

    public void setCourse(String course) { this.course = course; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String getRoleDetails() {
        return "Student - Course: " + course + ", Year: " + yearOfStudy;
    }

    @Override
    public String toString() {
        return super.toString() + " | " + course + " | Year " + yearOfStudy;
    }
}