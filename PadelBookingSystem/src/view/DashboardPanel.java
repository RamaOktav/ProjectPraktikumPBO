// src/view/DashboardPanel.java
package view;

import dao.BookingDAO;
import dao.CustomerDAO;
import dao.CourtDAO;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Selamat Datang di Sistem Pemesanan Padel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        int totalCustomer = new CustomerDAO().getAll().size();
        int totalCourt = new CourtDAO().getAll().size();
        int totalBooking = new BookingDAO().getAll().size();

        cards.add(createCard("Total Pelanggan", String.valueOf(totalCustomer), new Color(52, 152, 219)));
        cards.add(createCard("Total Lapangan", String.valueOf(totalCourt), new Color(46, 204, 113)));
        cards.add(createCard("Total Pemesanan", String.valueOf(totalBooking), new Color(155, 89, 182)));

        add(cards, BorderLayout.CENTER);
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