// src/model/Admin.java  ← INHERITANCE
package model;

public class Admin extends Person {
    private String username;
    private String password;

    public Admin(int id, String name, String phone, String email,
                 String username, String password) {
        super(id, name, phone, email);
        this.username = username;
        this.password = password;
    }

    @Override
    public String getRole() { return "Admin"; } // POLYMORPHISM

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}