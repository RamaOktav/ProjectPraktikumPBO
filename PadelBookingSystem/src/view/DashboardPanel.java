package view;

import controller.DashboardController;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();

    // Label nilai disimpan sebagai field agar bisa diupdate dari SwingWorker
    private final JLabel valCustomers, valCourts, valBookings;
    private final JButton btnRefresh;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Title ──
        JLabel title = new JLabel(
            "Selamat Datang di Sistem Pemesanan Padel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // ── Refresh button ──
        btnRefresh = new JButton("⟳  Refresh");
        btnRefresh.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(100, 100, 100));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadStatsAsync());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title,      BorderLayout.CENTER);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        // ── Cards (dibuat sekali, label nilai diupdate saat refresh) ──
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        valCustomers = new JLabel("...", SwingConstants.CENTER);
        valCourts    = new JLabel("...", SwingConstants.CENTER);
        valBookings  = new JLabel("...", SwingConstants.CENTER);

        cardsPanel.add(buildCard("Total Pelanggan", valCustomers, new Color(52, 152, 219)));
        cardsPanel.add(buildCard("Total Lapangan",  valCourts,    new Color(46, 204, 113)));
        cardsPanel.add(buildCard("Total Pemesanan", valBookings,  new Color(155, 89, 182)));

        add(topPanel,   BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);

        // Load pertama kali saat panel dibuka
        loadStatsAsync();
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /**
     * Menjalankan getAllStats() di SwingWorker (background thread),
     * lalu update label di EDT setelah selesai.
     * Controller sudah menjalankan 3 query secara paralel via ExecutorService.
     */
    private void loadStatsAsync() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("Memuat...");

        // Set placeholder saat loading
        valCustomers.setText("...");
        valCourts   .setText("...");
        valBookings .setText("...");

        new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                // getAllStats() menjalankan 3 query paralel di thread pool controller
                return controller.getAllStats();
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    valCustomers.setText(String.valueOf(stats[0]));
                    valCourts   .setText(String.valueOf(stats[1]));
                    valBookings .setText(String.valueOf(stats[2]));
                } catch (Exception ex) {
                    valCustomers.setText("!");
                    valCourts   .setText("!");
                    valBookings .setText("!");
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                        "Gagal memuat statistik: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("⟳  Refresh");
                }
            }
        }.execute();
    }

    /** Membangun card dengan label nilai yang bisa diupdate. */
    private JPanel buildCard(String label, JLabel valLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));

        valLabel.setForeground(Color.WHITE);
        valLabel.setFont(new Font("Arial", Font.BOLD, 36));

        card.add(lbl,      BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }
    
    public void shutdown() {
    controller.shutdown();
}
}