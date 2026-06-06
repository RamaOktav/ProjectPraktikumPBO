package controller;

import dao.BookingDAO;
import dao.CourtDAO;
import dao.CustomerDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DashboardController {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CourtDAO    courtDAO    = new CourtDAO();
    private final BookingDAO  bookingDAO  = new BookingDAO();

    // Thread pool tetap — dibuat sekali, dipakai ulang
    private final ExecutorService pool = Executors.newFixedThreadPool(3);

    /**
     * Menjalankan 3 query secara paralel di thread pool.
     * @return int[0] = total customers, [1] = total courts, [2] = total bookings
     */
    public int[] getAllStats() {
        try {
            Future<Integer> fCustomers = pool.submit(() -> customerDAO.getAll().size());
            Future<Integer> fCourts    = pool.submit(() -> courtDAO.getAll().size());
            Future<Integer> fBookings  = pool.submit(() -> bookingDAO.getAll().size());

            return new int[]{
                fCustomers.get(),
                fCourts.get(),
                fBookings.get()
            };
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0, 0};
        }
    }

    /**
     * Panggil saat aplikasi ditutup agar thread pool bersih.
     * Tambahkan di MainFrame: controller.shutdown() pada windowClosing.
     */
    public void shutdown() {
        pool.shutdown();
    }
}