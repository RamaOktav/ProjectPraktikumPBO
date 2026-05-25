package controller;

import dao.BookingDAO;
import dao.CourtDAO;
import dao.CustomerDAO;
import model.Booking;
import model.Court;
import model.Customer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller for Booking management.
 * Contains all validation and business logic.
 */
public class BookingController {

    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CourtDAO    courtDAO    = new CourtDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── Data retrieval ──────────────────────────────────────────────

    public List<Booking>  getAllBookings()     { return bookingDAO.getAll(); }
    public List<Customer> getAllCustomers()    { return customerDAO.getAll(); }

    /** Only courts with status = Available (for the booking form combo box). */
    public List<Court> getAvailableCourts()   { return courtDAO.getAvailable(); }

    /** All courts (used when populating the update-status form). */
    public List<Court> getAllCourts()          { return courtDAO.getAll(); }

    // ── Business logic ──────────────────────────────────────────────

    /**
     * Calculates total price: pricePerHour × duration in hours.
     * Returns -1 if times are invalid or end <= start.
     */
    public double calculateTotal(Court court, String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FMT);
            LocalTime end   = LocalTime.parse(endTime,   TIME_FMT);
            long minutes = ChronoUnit.MINUTES.between(start, end);
            if (minutes <= 0) return -1;
            double hours = minutes / 60.0;
            return court.getPricePerHour() * hours;
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    /**
     * Validates inputs and creates a new booking.
     * @return null on success, or an error message string on failure.
     */
    public String addBooking(int customerId, int courtId,
                              String date, String startTime, String endTime,
                              double total) {

        // 1. Validate date
        try { LocalDate.parse(date, DATE_FMT); }
        catch (DateTimeParseException e) {
            return "Format tanggal salah! Gunakan YYYY-MM-DD.";
        }

        // 2. Validate times
        LocalTime start, end;
        try {
            start = LocalTime.parse(startTime, TIME_FMT);
            end   = LocalTime.parse(endTime,   TIME_FMT);
        } catch (DateTimeParseException e) {
            return "Format jam salah! Gunakan HH:mm (contoh: 08:00).";
        }

        // 3. End must be after start
        if (!end.isAfter(start)) {
            return "Jam selesai harus lebih besar dari jam mulai!";
        }

        // 4. Minimum 1 hour
        long minutes = ChronoUnit.MINUTES.between(start, end);
        if (minutes < 60) {
            return "Minimum pemesanan adalah 1 jam!";
        }

        // 5. Check scheduling conflict
        if (bookingDAO.isConflict(courtId, date, startTime, endTime, 0)) {
            return "Lapangan sudah dipesan pada waktu tersebut! Pilih jam lain.";
        }

        // 6. Insert
        Booking b = new Booking(0, customerId, courtId, date, startTime, endTime, total, "Pending");
        boolean ok = bookingDAO.insert(b);
        return ok ? null : "Gagal menyimpan pemesanan ke database!";
    }

    /**
     * Updates only the status of an existing booking.
     * @return null on success, or an error message on failure.
     */
    public String updateBookingStatus(int id, String status) {
        Booking b = new Booking(id, 0, 0, "", "", "", 0, status);
        return bookingDAO.update(b) ? null : "Gagal memperbarui status!";
    }

    /** Deletes a booking by ID. */
    public boolean deleteBooking(int id) {
        return bookingDAO.delete(id);
    }
}
