// src/model/Customer.java  ← INHERITANCE
package model;

public class Customer extends Person {
    public Customer(int id, String name, String phone, String email) {
        super(id, name, phone, email);
    }

    @Override
    public String getRole() { return "Customer"; } // POLYMORPHISM

    @Override
    public String toString() { return getName() + " (" + getPhone() + ")"; }
}