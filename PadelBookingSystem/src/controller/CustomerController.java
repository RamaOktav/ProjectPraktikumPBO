package controller;

import dao.CustomerDAO;
import model.Customer;
import java.util.List;

/**
 * Controller for Customer management.
 * Mediates between CustomerPanel (View) and CustomerDAO (Model).
 */
public class CustomerController {

    private final CustomerDAO dao = new CustomerDAO();

    /** Returns all customers from the database. */
    public List<Customer> getAllCustomers() {
        return dao.getAll();
    }

    /**
     * Validates and adds a new customer.
     * @return true if successful, false if validation fails or DB error
     */
    public boolean addCustomer(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return false;
        return dao.insert(new Customer(0, name.trim(), phone.trim(), email.trim()));
    }

    /** Updates an existing customer by ID. */
    public boolean updateCustomer(int id, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return false;
        return dao.update(new Customer(id, name.trim(), phone.trim(), email.trim()));
    }

    /** Deletes a customer by ID. */
    public boolean deleteCustomer(int id) {
        return dao.delete(id);
    }
}
