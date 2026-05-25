package view;

import controller.DashboardController;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();
    private JPanel cardsPanel;

    // Label yang akan diupdate setelah data selesai dimuat
    private JLabel valCustomers, valCourts, valBookings;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(
            "Selamat Datang di Sistem Pemesanan Padel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnRefresh = new JButton("⟳  Refresh");
        btnRefresh.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(100, 100, 100));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> {
            btnRefresh.setEnabled(false);           // cegah double-click
            btnRefresh.setText("Memuat...");
            loadStatsAsync(btnRefresh);             // jalankan di background thread
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Buat card sekali, simpan referensi label nilainya
        JPanel[] cards = {
            createCard("Total Pelanggan", new Color(52, 152, 219)),
            createCard("Total Lapangan",  new Color(46, 204, 113)),
            createCard("Total Pemesanan", new Color(155, 89, 182))
        };
        // valCustomers, valCourts, valBookings diset di createCard()
        for (JPanel card : cards) cardsPanel.add(card);

        add(topPanel,   BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);

        loadStatsAsync(btnRefresh); // load pertama kali saat panel dibuka
    }

    /**
     * Menjalankan query DB di background thread (bukan EDT),
     * lalu update label di EDT setelah selesai.
     */
    private void loadStatsAsync(JButton btnRefresh) {
        new SwingWorker<int[], Void>() {

            @Override
            protected int[] doInBackground() {
                // Ini jalan di background thread — aman untuk query DB
                return controller.getAllStats(); // paralel query dari DashboardController
            }

            @Override
            protected void done() {
                // Ini jalan di EDT — aman untuk update UI
                try {
                    int[] stats = get();
                    valCustomers.setText(String.valueOf(stats[0]));
                    valCourts   .setText(String.valueOf(stats[1]));
                    valBookings .setText(String.valueOf(stats[2]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("⟳  Refresh");
                }
            }
        }.execute();
    }

    /** Buat card + simpan referensi label nilai (valCustomers dst.) */
    private JPanel createCard(String label, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel val = new JLabel("...", SwingConstants.CENTER); // placeholder saat loading
        val.setForeground(Color.WHITE);
        val.setFont(new Font("Arial", Font.BOLD, 36));

        // Simpan referensi ke field yang sesuai
        if (label.contains("Pelanggan")) valCustomers = val;
        else if (label.contains("Lapangan")) valCourts = val;
        else valBookings = val;

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }
}