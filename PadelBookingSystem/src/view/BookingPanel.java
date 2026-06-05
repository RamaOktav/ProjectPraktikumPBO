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

public class BookingPanel extends JPanel {

    private final BookingController controller = new BookingController();

    // Parallel lists — sumber data untuk combo box
    private final List<Customer> customerList = new ArrayList<>();
    private final List<Court>    courtList    = new ArrayList<>();

    // ── UI Components ─────────────────────────────────────────────
    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JComboBox<String> cbCustomer, cbCourt, cbStatus;
    private final JTextField        txtDate, txtStart, txtEnd;
    private final JLabel            lblTotal;
    private double                  calculatedTotal = 0;
    private int                     selectedId      = -1;

    private final JButton btnBook, btnUpdate, btnDelete, btnClear, btnRefresh;

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

        addFormRow(form, gbc, 0, "Pelanggan:",             cbCustomer);
        addFormRow(form, gbc, 1, "Lapangan:",              cbCourt);
        addFormRow(form, gbc, 2, "Tanggal (YYYY-MM-DD):",  txtDate);
        addFormRow(form, gbc, 3, "Jam Mulai (HH:mm):",     txtStart);
        addFormRow(form, gbc, 4, "Jam Selesai (HH:mm):",   txtEnd);
        addFormRow(form, gbc, 5, "Total Harga:",            lblTotal);
        addFormRow(form, gbc, 6, "Status:",                 cbStatus);

        // ── Buttons ─────────────────────────────────────────────────
        btnBook    = new JButton("✔  Pesan");
        btnUpdate  = new JButton("↺  Update Status");
        btnDelete  = new JButton("✖  Hapus");
        btnClear   = new JButton("⊘  Bersihkan");
        btnRefresh = new JButton("⟳  Refresh");

        btnBook   .setBackground(new Color(46, 139, 87));   btnBook   .setForeground(Color.WHITE);
        btnUpdate .setBackground(new Color(30, 100, 200));  btnUpdate .setForeground(Color.WHITE);
        btnDelete .setBackground(new Color(180, 40, 40));   btnDelete .setForeground(Color.WHITE);
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

        loadComboData();
        loadData();
        initEvents();
    }

    // ── Events ───────────────────────────────────────────────────────
    private void initEvents() {

        // Auto-hitung total saat lapangan atau jam berubah
        cbCourt.addActionListener(e -> recalcTotal());
        txtStart.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { recalcTotal(); }
        });
        txtEnd.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { recalcTotal(); }
        });

        // Pilih baris tabel → isi form
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

        // ── Pesan ────────────────────────────────────────────────────
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

            // Ambil semua nilai sebelum masuk background thread
            int customerId = customerList.get(custIdx).getId();
            int courtId    = courtList.get(courtIdx).getId();
            String date    = txtDate.getText().trim();
            String start   = txtStart.getText().trim();
            String end     = txtEnd.getText().trim();
            double total   = calculatedTotal;

            setFormEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return controller.addBooking(customerId, courtId, date, start, end, total);
                }
                @Override
                protected void done() {
                    try {
                        String err = get();
                        if (err == null) {
                            showInfo("Pemesanan berhasil!\nTotal: Rp " + CURRENCY.format(total));
                            clearForm();
                            loadData();
                        } else {
                            showError(err);
                        }
                    } catch (Exception ex) {
                        showError("Terjadi kesalahan: " + ex.getMessage());
                    } finally {
                        setFormEnabled(true);
                    }
                }
            }.execute();
        });

        // ── Update Status ─────────────────────────────────────────────
        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { showError("Pilih baris booking terlebih dahulu!"); return; }

            int id         = selectedId;
            String status  = (String) cbStatus.getSelectedItem();

            setFormEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return controller.updateBookingStatus(id, status);
                }
                @Override
                protected void done() {
                    try {
                        String err = get();
                        if (err == null) {
                            showInfo("Status berhasil diperbarui!");
                            clearForm();
                            loadData();
                        } else {
                            showError(err);
                        }
                    } catch (Exception ex) {
                        showError("Terjadi kesalahan: " + ex.getMessage());
                    } finally {
                        setFormEnabled(true);
                    }
                }
            }.execute();
        });

        // ── Hapus ─────────────────────────────────────────────────────
        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { showError("Pilih baris booking terlebih dahulu!"); return; }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus booking ini secara permanen?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            int id = selectedId;
            setFormEnabled(false);
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return controller.deleteBooking(id);
                }
                @Override
                protected void done() {
                    try {
                        String err = get();
                        if (err == null) {
                            showInfo("Booking berhasil dihapus.");
                            clearForm();
                            loadData();
                        } else {
                            showError(err);
                        }
                    } catch (Exception ex) {
                        showError("Terjadi kesalahan: " + ex.getMessage());
                    } finally {
                        setFormEnabled(true);
                    }
                }
            }.execute();
        });

        btnClear.addActionListener(e -> clearForm());

        btnRefresh.addActionListener(e -> { clearForm(); loadData(); });
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /** Load data booking dari DB di background thread. */
    private void loadData() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("Memuat...");

        new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() {
                return controller.getAllBookings();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Booking b : get()) {
                        tableModel.addRow(new Object[]{
                            b.getId(), b.getCustomerName(), b.getCourtName(),
                            b.getBookingDate(), b.getStartTime(), b.getEndTime(),
                            b.getTotalPrice(), b.getStatus()
                        });
                    }
                } catch (Exception ex) {
                    showError("Gagal memuat data: " + ex.getMessage());
                } finally {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("⟳  Refresh");
                }
            }
        }.execute();
    }

    /** Load combo customer & lapangan di background thread. */
    private void loadComboData() {
        new SwingWorker<Void, Void>() {
            private List<Customer> customers;
            private List<Court>    courts;

            @Override
            protected Void doInBackground() {
                customers = controller.getAllCustomers();
                courts    = controller.getAvailableCourts();
                return null;
            }
            @Override
            protected void done() {
                try {
                    get(); // lempar exception jika ada
                    customerList.clear();
                    courtList.clear();
                    cbCustomer.removeAllItems();
                    cbCourt.removeAllItems();

                    for (Customer c : customers) {
                        customerList.add(c);
                        cbCustomer.addItem(c.getName() + "  (" + c.getPhone() + ")");
                    }
                    for (Court c : courts) {
                        courtList.add(c);
                        cbCourt.addItem(c.getCourtName() + "  — Rp "
                            + CURRENCY.format(c.getPricePerHour()) + "/jam");
                    }
                    recalcTotal();
                } catch (Exception ex) {
                    showError("Gagal memuat data combo: " + ex.getMessage());
                }
            }
        }.execute();
    }

    /** Hitung ulang total harga dari lapangan terpilih × durasi. */
    private void recalcTotal() {
        int courtIdx = cbCourt.getSelectedIndex();
        if (courtIdx < 0 || courtIdx >= courtList.size()) {
            lblTotal.setText("Rp 0");
            calculatedTotal = 0;
            return;
        }
        double total = controller.calculateTotal(
            courtList.get(courtIdx),
            txtStart.getText().trim(),
            txtEnd.getText().trim());

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

    private void setFormEnabled(boolean enabled) {
        cbCustomer.setEnabled(enabled);
        cbCourt   .setEnabled(enabled);
        cbStatus  .setEnabled(enabled);
        txtDate   .setEnabled(enabled);
        txtStart  .setEnabled(enabled);
        txtEnd    .setEnabled(enabled);
        btnBook   .setEnabled(enabled);
        btnUpdate .setEnabled(enabled);
        btnDelete .setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
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
        loadComboData(); // refresh daftar lapangan available
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sukses", JOptionPane.INFORMATION_MESSAGE);
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