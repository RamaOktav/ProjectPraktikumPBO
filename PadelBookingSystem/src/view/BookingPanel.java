// src/view/BookingPanel.java
package view;

import dao.BookingDAO;
import dao.CourtDAO;
import dao.CustomerDAO;
import model.Booking;
import model.Court;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingPanel extends JPanel {
    private BookingDAO bookingDAO = new BookingDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private CourtDAO courtDAO = new CourtDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> cbCustomer, cbCourt, cbStatus;
    private JTextField txtDate, txtStart, txtEnd, txtTotal;
    private int selectedId = -1;

    public BookingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Form Pemesanan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbCustomer = new JComboBox<>();
        cbCourt    = new JComboBox<>();
        cbStatus   = new JComboBox<>(new String[]{"Pending","Confirmed","Cancelled","Done"});
        txtDate    = new JTextField("2025-01-01");
        txtStart   = new JTextField("08:00");
        txtEnd     = new JTextField("10:00");
        txtTotal   = new JTextField("0");

        loadComboData();

        String[] lbls = {"Pelanggan:", "Lapangan:", "Tanggal:", "Jam Mulai:", "Jam Selesai:", "Total (Rp):", "Status:"};
        JComponent[] cmps = {cbCustomer, cbCourt, txtDate, txtStart, txtEnd, txtTotal, cbStatus};
        for (int i = 0; i < lbls.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(new JLabel(lbls[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(cmps[i], gbc);
        }

        JButton btnAdd    = new JButton("Pesan");
        JButton btnUpdate = new JButton("Update Status");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear  = new JButton("Bersihkan");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = lbls.length; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        tableModel = new DefaultTableModel(
            new String[]{"ID","Pelanggan","Lapangan","Tanggal","Mulai","Selesai","Total","Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtDate.setText((String) tableModel.getValueAt(row, 3));
                txtStart.setText((String) tableModel.getValueAt(row, 4));
                txtEnd.setText((String) tableModel.getValueAt(row, 5));
                txtTotal.setText(String.valueOf(tableModel.getValueAt(row, 6)));
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 7));
            }
        });

        btnAdd.addActionListener(e -> {
            int custIdx = cbCustomer.getSelectedIndex();
            int courtIdx = cbCourt.getSelectedIndex();
            if (custIdx < 0 || courtIdx < 0) return;
            List<Customer> custs = customerDAO.getAll();
            List<Court> courts = courtDAO.getAll();
            Booking b = new Booking(0, custs.get(custIdx).getId(),
                courts.get(courtIdx).getId(), txtDate.getText(),
                txtStart.getText(), txtEnd.getText(),
                Double.parseDouble(txtTotal.getText()), "Pending");
            if (bookingDAO.insert(b)) {
                JOptionPane.showMessageDialog(this, "Pemesanan berhasil!");
                clearForm(); loadData();
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) return;
            Booking b = new Booking(selectedId, 0, 0, "", "", "", 0, (String) cbStatus.getSelectedItem());
            if (bookingDAO.update(b)) {
                JOptionPane.showMessageDialog(this, "Status diperbarui!");
                clearForm(); loadData();
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId < 0) return;
            if (JOptionPane.showConfirmDialog(this, "Hapus pemesanan ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (bookingDAO.delete(selectedId)) {
                    clearForm(); loadData();
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private void loadComboData() {
        cbCustomer.removeAllItems();
        cbCourt.removeAllItems();
        for (Customer c : customerDAO.getAll()) cbCustomer.addItem(c.getName());
        for (Court c : courtDAO.getAll()) cbCourt.addItem(c.getCourtName());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Booking b : bookingDAO.getAll()) {
            tableModel.addRow(new Object[]{b.getId(), b.getCustomerName(),
                b.getCourtName(), b.getBookingDate(), b.getStartTime(),
                b.getEndTime(), b.getTotalPrice(), b.getStatus()});
        }
    }

    private void clearForm() {
        selectedId = -1; table.clearSelection();
        txtDate.setText("2025-01-01"); txtStart.setText("08:00");
        txtEnd.setText("10:00"); txtTotal.setText("0");
    }
}