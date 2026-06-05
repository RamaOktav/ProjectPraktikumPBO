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

public class BookingController {

    private final BookingDAO  bookingDAO  = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CourtDAO    courtDAO    = new CourtDAO();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ── Data retrieval ──────────────────────────────────────────────

    public List<Booking>  getAllBookings()   { return bookingDAO.getAll(); }
    public List<Customer> getAllCustomers()  { return customerDAO.getAll(); }
    public List<Court>    getAvailableCourts() { return courtDAO.getAvailable(); }
    public List<Court>    getAllCourts()     { return courtDAO.getAll(); }

    // ── Business logic ──────────────────────────────────────────────

    /**
     * Kalkulasi total harga: pricePerHour × durasi (jam).
     * @return total harga, atau -1 jika waktu tidak valid / end <= start
     */
    public double calculateTotal(Court court, String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FMT);
            LocalTime end   = LocalTime.parse(endTime,   TIME_FMT);
            long minutes = ChronoUnit.MINUTES.between(start, end);
            if (minutes <= 0) return -1;
            return court.getPricePerHour() * (minutes / 60.0);
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    /**
     * Validasi input lalu buat booking baru.
     * @return null jika sukses, atau pesan error jika gagal
     */
    public String addBooking(int customerId, int courtId,
                             String date, String startTime, String endTime,
                             double total) {

        // 1. Validasi format tanggal
        try { LocalDate.parse(date, DATE_FMT); }
        catch (DateTimeParseException e) {
            return "Format tanggal salah! Gunakan YYYY-MM-DD.";
        }

        // 2. Validasi format jam
        LocalTime start, end;
        try {
            start = LocalTime.parse(startTime, TIME_FMT);
            end   = LocalTime.parse(endTime,   TIME_FMT);
        } catch (DateTimeParseException e) {
            return "Format jam salah! Gunakan HH:mm (contoh: 08:00).";
        }

        // 3. Jam selesai harus setelah jam mulai
        if (!end.isAfter(start)) {
            return "Jam selesai harus lebih besar dari jam mulai!";
        }

        // 4. Minimum 1 jam
        if (ChronoUnit.MINUTES.between(start, end) < 60) {
            return "Minimum pemesanan adalah 1 jam!";
        }

        // 5. Cek konflik jadwal
        if (bookingDAO.isConflict(courtId, date, startTime, endTime, 0)) {
            return "Lapangan sudah dipesan pada waktu tersebut! Pilih jam lain.";
        }

        // 6. Simpan ke DB
        boolean ok = bookingDAO.insert(
            new Booking(0, customerId, courtId, date, startTime, endTime, total, "Pending"));
        return ok ? null : "Gagal menyimpan pemesanan ke database!";
    }

    /**
     * Update status booking.
     * @return null jika sukses, atau pesan error jika gagal
     */
    public String updateBookingStatus(int id, String status) {
        boolean ok = bookingDAO.update(
            new Booking(id, 0, 0, "", "", "", 0, status));
        return ok ? null : "Gagal memperbarui status!";
    }

    /**
     * Hapus booking berdasarkan ID.
     * @return null jika sukses, atau pesan error jika gagal
     */
    public String deleteBooking(int id) {
        boolean ok = bookingDAO.delete(id);
        return ok ? null : "Gagal menghapus booking!";
    }
}