package view;

import controller.DashboardController;
import javax.swing.*;
import java.awt.*;

/**
 * View for Dashboard.
 * Refresh button rebuilds the stat cards from the controller.
 */
public class DashboardPanel extends JPanel {

    private final DashboardController controller = new DashboardController();
    private JPanel cardsPanel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Title ──
        JLabel title = new JLabel(
            "Selamat Datang di Sistem Pemesanan Padel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        // ── Refresh button ──
        JButton btnRefresh = new JButton("⟳  Refresh");
        btnRefresh.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(100, 100, 100));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> refreshCards());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(btnRefresh, BorderLayout.EAST);

        // ── Cards ──
        cardsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        add(topPanel,    BorderLayout.NORTH);
        add(cardsPanel,  BorderLayout.CENTER);

        refreshCards();
    }

    private void refreshCards() {
        cardsPanel.removeAll();
        cardsPanel.add(createCard("Total Pelanggan",
            String.valueOf(controller.getTotalCustomers()),
            new Color(52, 152, 219)));
        cardsPanel.add(createCard("Total Lapangan",
            String.valueOf(controller.getTotalCourts()),
            new Color(46, 204, 113)));
        cardsPanel.add(createCard("Total Pemesanan",
            String.valueOf(controller.getTotalBookings()),
            new Color(155, 89, 182)));
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setForeground(Color.WHITE);
        val.setFont(new Font("Arial", Font.BOLD, 36));

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }
}
