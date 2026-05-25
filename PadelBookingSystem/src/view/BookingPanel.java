package view;

import controller.BookingController;
import model.Booking;
import model.Court;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * View for Booking management.
 * All validation and business logic delegated to BookingController.
 *
 * Fixes applied:
 *  - Customer & Court stored in parallel lists (not re-fetched by index on click)
 *  - Total price auto-calculated from court price × hours
 *  - Date defaults to today
 *  - Time validation and conflict check handled in controller
 *  - Only "Available" courts shown in booking form
 */
public class BookingPanel extends JPanel {

    // ─── Controller ────────────────────────────────────────────────
    private final BookingController controller = new BookingController();

    // ─── Parallel lists — source of truth for combo selections ─────
    private final List<Customer> customerList = new ArrayList<>();
    private final List<Court>    courtList    = new ArrayList<>();

    // ─── UI Components ─────────────────────────────────────────────
    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JComboBox<String> cbCustomer, cbCourt, cbStatus;
    private final JTextField        txtDate, txtStart, txtEnd;
    private final JLabel            lblTotal;       // read-only calculated total
    private double                  calculatedTotal = 0;
    private int                     selectedId      = -1;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final NumberFormat CURRENCY =
        NumberFormat.getNumberInstance(new Locale("id", "ID"));

    public BookingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Form ────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Form Pemesanan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        cbCustomer = new JComboBox<>();
        cbCourt    = new JComboBox<>();
        cbStatus   = new JComboBox<>(new String[]{"Pending", "Confirmed", "Cancelled", "Done"});
        txtDate    = new JTextField(LocalDate.now().format(DATE_FMT));
        txtStart   = new JTextField("08:00");
        txtEnd     = new JTextField("10:00");
        lblTotal   = new JLabel("Rp 0", SwingConstants.LEFT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotal.setForeground(new Color(0, 128, 0));

        loadComboData();   // populate customerList, courtList, cbCustomer, cbCourt

        // Row layout: label | component
        addFormRow(form, gbc, 0, "Pelanggan:",    cbCustomer);
        addFormRow(form, gbc, 1, "Lapangan:",     cbCourt);
        addFormRow(form, gbc, 2, "Tanggal (YYYY-MM-DD):", txtDate);
        addFormRow(form, gbc, 3, "Jam Mulai (HH:mm):",   txtStart);
        addFormRow(form, gbc, 4, "Jam Selesai (HH:mm):",  txtEnd);
        addFormRow(form, gbc, 5, "Total Harga:",  lblTotal);   // read-only
        addFormRow(form, gbc, 6, "Status:",       cbStatus);

        // ── Buttons ─────────────────────────────────────────────────
        JButton btnBook    = new JButton("✔  Pesan");
        JButton btnUpdate  = new JButton("↺  Update Status");
        JButton btnDelete  = new JButton("✖  Hapus");
        JButton btnClear   = new JButton("⊘  Bersihkan");
        JButton btnRefresh = new JButton("⟳  Refresh");

        btnBook   .setBackground(new Color(46, 139, 87));  btnBook   .setForeground(Color.WHITE);
        btnUpdate .setBackground(new Color(30, 100, 200)); btnUpdate .setForeground(Color.WHITE);
        btnDelete .setBackground(new Color(180, 40, 40));  btnDelete .setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(100, 100, 100)); btnRefresh.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.add(btnBook); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear); btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weightx = 1;
        form.add(btnPanel, gbc);

        // ── Table ────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Pelanggan", "Lapangan", "Tanggal",
                         "Mulai", "Selesai", "Total (Rp)", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        // ── Auto-calculate total when court or time changes ──────────
        cbCourt.addActionListener(e  -> recalcTotal());
        txtStart.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { recalcTotal(); }
        });
        txtEnd.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { recalcTotal(); }
        });

        // ── Select row → fill form ────────────────────────────────────
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtDate .setText((String) tableModel.getValueAt(row, 3));
                txtStart.setText((String) tableModel.getValueAt(row, 4));
                txtEnd  .setText((String) tableModel.getValueAt(row, 5));
                Object rawTotal = tableModel.getValueAt(row, 6);
                double t = rawTotal instanceof Number ? ((Number) rawTotal).doubleValue() : 0;
                lblTotal.setText("Rp " + CURRENCY.format(t));
                calculatedTotal = t;
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 7));
            }
        });

        // ── BOOK ─────────────────────────────────────────────────────
        btnBook.addActionListener(e -> {
            int custIdx  = cbCustomer.getSelectedIndex();
            int courtIdx = cbCourt.getSelectedIndex();

            if (custIdx < 0 || custIdx >= customerList.size()) {
                showError("Pilih pelanggan terlebih dahulu!"); return;
            }
            if (courtIdx < 0 || courtIdx >= courtList.size()) {
                showError("Pilih lapangan terlebih dahulu!"); return;
            }

            recalcTotal();
            if (calculatedTotal <= 0) {
                showError("Periksa jam mulai dan jam selesai — total tidak valid!"); return;
            }

            String err = controller.addBooking(
                customerList.get(custIdx).getId(),
                courtList.get(courtIdx).getId(),
                txtDate.getText().trim(),
                txtStart.getText().trim(),
                txtEnd.getText().trim(),
                calculatedTotal);

            if (err == null) {
                JOptionPane.showMessageDialog(this,
                    "Pemesanan berhasil!\nTotal: Rp " + CURRENCY.format(calculatedTotal),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); loadData();
            } else {
                showError(err);
            }
        });

        // ── UPDATE STATUS ─────────────────────────────────────────────
        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { showError("Pilih baris booking terlebih dahulu!"); return; }
            String err = controller.updateBookingStatus(
                selectedId, (String) cbStatus.getSelectedItem());
            if (err == null) {
                JOptionPane.showMessageDialog(this, "Status berhasil diperbarui!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearForm(); loadData();
            } else {
                showError(err);
            }
        });

        // ── DELETE ────────────────────────────────────────────────────
        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { showError("Pilih baris booking terlebih dahulu!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus booking ini secara permanen?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteBooking(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Booking dihapus.");
                    clearForm(); loadData();
                } else {
                    showError("Gagal menghapus booking!");
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnRefresh.addActionListener(e -> {
            clearForm();
            loadData();
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.",
                "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /** Populates parallel lists and combo boxes. Only available courts for booking. */
    private void loadComboData() {
        customerList.clear();
        courtList.clear();
        cbCustomer.removeAllItems();
        cbCourt.removeAllItems();

        for (Customer c : controller.getAllCustomers()) {
            customerList.add(c);
            cbCustomer.addItem(c.getName() + "  (" + c.getPhone() + ")");
        }
        for (Court c : controller.getAvailableCourts()) {
            courtList.add(c);
            cbCourt.addItem(c.getCourtName() + "  — Rp " + CURRENCY.format(c.getPricePerHour()) + "/jam");
        }
    }

    /** Recalculates total price from selected court's price × duration. */
    private void recalcTotal() {
        int courtIdx = cbCourt.getSelectedIndex();
        if (courtIdx < 0 || courtIdx >= courtList.size()) {
            lblTotal.setText("Rp 0");
            calculatedTotal = 0;
            return;
        }
        Court court = courtList.get(courtIdx);
        double total = controller.calculateTotal(
            court, txtStart.getText().trim(), txtEnd.getText().trim());
        if (total > 0) {
            calculatedTotal = total;
            lblTotal.setText("Rp " + CURRENCY.format(total));
            lblTotal.setForeground(new Color(0, 128, 0));
        } else {
            calculatedTotal = 0;
            lblTotal.setText("— (periksa jam mulai/selesai)");
            lblTotal.setForeground(Color.RED);
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Booking b : controller.getAllBookings()) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getCustomerName(), b.getCourtName(),
                b.getBookingDate(), b.getStartTime(), b.getEndTime(),
                b.getTotalPrice(), b.getStatus()
            });
        }
    }

    private void clearForm() {
        selectedId = -1;
        table.clearSelection();
        txtDate.setText(LocalDate.now().format(DATE_FMT));
        txtStart.setText("08:00");
        txtEnd.setText("10:00");
        lblTotal.setText("Rp 0");
        lblTotal.setForeground(new Color(0, 128, 0));
        calculatedTotal = 0;
        cbStatus.setSelectedIndex(0);
        loadComboData();   // refresh available courts list
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc,
                             int row, String label, JComponent comp) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(comp, gbc);
    }
}
