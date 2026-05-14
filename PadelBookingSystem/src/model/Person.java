// src/model/Person.java  ← ABSTRACTION (abstract class)
package model;

public abstract class Person {
    // ENCAPSULATION: private fields + getter/setter
    private int id;
    private String name;
    private String phone;
    private String email;

    public Person(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Abstract method — wajib diimplementasi anak class (ABSTRACTION)
    public abstract String getRole();

    // Getters & Setters (ENCAPSULATION)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // POLYMORPHISM: method bisa di-override
    @Override
    public String toString() {
        return "[" + getRole() + "] " + name;
    }
}