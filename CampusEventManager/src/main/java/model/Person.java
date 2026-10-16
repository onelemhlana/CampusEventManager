package model;


public abstract class Person {
    protected String id;
    protected String name;
    protected String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Abstract method – must be implemented by subclasses.
     * Demonstrates LU3 abstract class concept.
     */
    public abstract String getRoleDetails();

    /**
     * Overridden in subclasses; base version for polymorphism demos.
     */
    public String getRole() {
        return "Person";
    }

    @Override
    public String toString() {
        return id + " | " + name + " | " + email;
    }
}