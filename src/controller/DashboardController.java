package controller;

import dao.BookingDAO;
import dao.CourtDAO;
import dao.CustomerDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DashboardController {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CourtDAO courtDAO       = new CourtDAO();
    private final BookingDAO bookingDAO   = new BookingDAO();

    // Satu thread pool dipakai ulang, bukan dibuat setiap kali
    private final ExecutorService pool = Executors.newFixedThreadPool(3);

    public int[] getAllStats() {
        try {
            Future<Integer> fCustomers = pool.submit(() -> customerDAO.getAll().size());
            Future<Integer> fCourts    = pool.submit(() -> courtDAO.getAll().size());
            Future<Integer> fBookings  = pool.submit(() -> bookingDAO.getAll().size());

            // .get() menunggu hasil masing-masing thread selesai
            return new int[]{
                fCustomers.get(),  // index 0 = total customers
                fCourts.get(),     // index 1 = total courts
                fBookings.get()    // index 2 = total bookings
            };

        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0, 0}; // fallback kalau ada error
        }
    }

    // Panggil ini saat aplikasi ditutup agar thread pool bersih
    public void shutdown() {
        pool.shutdown();
    }
}