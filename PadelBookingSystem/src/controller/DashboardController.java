package controller;

import dao.BookingDAO;
import dao.CourtDAO;
import dao.CustomerDAO;

/**
 * Controller for Dashboard.
 * Provides aggregate statistics to DashboardPanel (View).
 */
public class DashboardController {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CourtDAO courtDAO       = new CourtDAO();
    private final BookingDAO bookingDAO   = new BookingDAO();

    public int getTotalCustomers() {
        return customerDAO.getAll().size();
    }

    public int getTotalCourts() {
        return courtDAO.getAll().size();
    }

    public int getTotalBookings() {
        return bookingDAO.getAll().size();
    }
}
