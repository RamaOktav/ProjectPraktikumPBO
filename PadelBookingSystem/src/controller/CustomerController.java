package controller;

import dao.CustomerDAO;
import model.Customer;
import java.util.List;


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
    public int addCustomer(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return -1;
        boolean ok = dao.insert(new Customer(0, name.trim(), phone.trim(), email.trim()));
        return ok? 1:0;
    }

    /** Updates an existing customer by ID. */
    public int updateCustomer(int id, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return -1;
        boolean ok = dao.update(new Customer(0, name.trim(), phone.trim(), email.trim()));
        return ok? 1:0;
    }

    /** Deletes a customer by ID. */
    public boolean deleteCustomer(int id) {
        return dao.delete(id);
    }
}
