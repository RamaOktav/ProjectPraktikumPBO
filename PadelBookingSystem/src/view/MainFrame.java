// src/view/MainFrame.java
package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final DashboardPanel dashboardPanel = new DashboardPanel();

    public MainFrame(String username) {
        setTitle("Sistem Pemesanan Lapangan Padel");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ganti dari EXIT_ON_CLOSE
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard",  dashboardPanel);
        tabs.addTab("Pelanggan",  new CustomerPanel());
        tabs.addTab("Lapangan",   new CourtPanel());
        tabs.addTab("Pemesanan",  new BookingPanel());

        add(tabs);

        // Pastikan thread pool controller dimatikan saat aplikasi ditutup
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dashboardPanel.shutdown();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}