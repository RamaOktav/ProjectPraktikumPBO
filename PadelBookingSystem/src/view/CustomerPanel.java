package view;

import controller.CustomerController;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerController controller = new CustomerController();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField txtName, txtPhone, txtEmail;
    private int selectedId = -1;

    // Tombol disimpan sebagai field agar bisa di-enable/disable
    private final JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Pelanggan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtName  = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);

        String[]     labels = {"Nama:", "Telepon:", "Email:"};
        JTextField[] fields = {txtName, txtPhone, txtEmail};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
        }

        // ── Buttons ──
        btnAdd     = new JButton("Tambah");
        btnUpdate  = new JButton("Update");
        btnDelete  = new JButton("Hapus");
        btnClear   = new JButton("Bersihkan");
        btnRefresh = new JButton("⟳ Refresh");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear); btnPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        // ── Table ──
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama", "Telepon", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
        initEvents();
    }

    // ── Events ───────────────────────────────────────────────────────
    private void initEvents() {

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName .setText((String) tableModel.getValueAt(row, 1));
                txtPhone.setText((String) tableModel.getValueAt(row, 2));
                txtEmail.setText((String) tableModel.getValueAt(row, 3));
            }
        });

        // ── Tambah ──
        btnAdd.addActionListener(e -> {
            String name  = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();

            setFormEnabled(false);
            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() {
                    return controller.addCustomer(name, phone, email);
                }
                @Override
                protected void done() {
                    try {
                        switch (get()) {
                            case  1 -> { showInfo("Pelanggan berhasil ditambahkan!"); clearForm(); loadData(); }
                            case  0 -> showError("Email sudah digunakan!");
                            case -1 -> showError("Nama tidak boleh kosong!");
                        }
                    } catch (Exception ex) {
                        showError("Terjadi kesalahan: " + ex.getMessage());
                    } finally {
                        setFormEnabled(true);
                    }
                }
            }.execute();
        });

        // ── Update ──
        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { showInfo("Pilih data dulu!"); return; }
            String name  = txtName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            int id = selectedId;

            setFormEnabled(false);
            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() {
                    return controller.updateCustomer(id, name, phone, email);
                }
                @Override
                protected void done() {
                    try {
                        switch (get()) {
                            case  1 -> { showInfo("Data berhasil diupdate!"); clearForm(); loadData(); }
                            case  0 -> showError("Email sudah digunakan!");
                            case -1 -> showError("Nama tidak boleh kosong!");
                        }
                    } catch (Exception ex) {
                        showError("Terjadi kesalahan: " + ex.getMessage());
                    } finally {
                        setFormEnabled(true);
                    }
                }
            }.execute();
        });

        // ── Hapus ──
        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { showInfo("Pilih data dulu!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            int id = selectedId;
            setFormEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return controller.deleteCustomer(id);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) { showInfo("Data berhasil dihapus!"); clearForm(); loadData(); }
                        else showError("Gagal menghapus data!");
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

    /** Load data dari DB di background thread, update tabel di EDT. */
    private void loadData() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("Memuat...");

        new SwingWorker<List<Customer>, Void>() {
            @Override
            protected List<Customer> doInBackground() {
                return controller.getAllCustomers();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Customer c : get()) {
                        tableModel.addRow(new Object[]{
                            c.getId(), c.getName(), c.getPhone(), c.getEmail()
                        });
                    }
                } catch (Exception ex) {
                    showError("Gagal memuat data: " + ex.getMessage());
                } finally {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("⟳ Refresh");
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        txtName .setEnabled(enabled);
        txtPhone.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        btnAdd    .setEnabled(enabled);
        btnUpdate .setEnabled(enabled);
        btnDelete .setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
    }

    private void clearForm() {
        txtName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}