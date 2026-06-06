package view;

import controller.CourtController;
import model.Court;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourtPanel extends JPanel {

    private final CourtController controller = new CourtController();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField txtName, txtType, txtPrice;
    private final JComboBox<String> cbStatus;
    private int selectedId = -1;

    private final JButton btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh;

    public CourtPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Lapangan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtName  = new JTextField(20);
        txtType  = new JTextField(20);
        txtPrice = new JTextField(20);
        cbStatus = new JComboBox<>(new String[]{"Available", "Unavailable"});

        String[]     labels = {"Nama Lapangan:", "Tipe:", "Harga/Jam (Rp):", "Status:"};
        JComponent[] fields = {txtName, txtType, txtPrice, cbStatus};

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

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        // ── Table ──
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama Lapangan", "Tipe", "Harga/Jam", "Status"}, 0) {
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
                txtType .setText((String) tableModel.getValueAt(row, 2));
                txtPrice.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                cbStatus.setSelectedItem(tableModel.getValueAt(row, 4));
            }
        });

        // ── Tambah ──
        btnAdd.addActionListener(e -> {
            String name   = txtName.getText().trim();
            String type   = txtType.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            double price;
            try {
                price = Double.parseDouble(txtPrice.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Harga harus berupa angka!"); return;
            }

            setFormEnabled(false);
            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() {
                    return controller.addCourt(name, type, price, status);
                }
                @Override
                protected void done() {
                    try {
                        switch (get()) {
                            case  1 -> { showInfo("Lapangan berhasil ditambahkan!"); clearForm(); loadData(); }
                            case  0 -> showError("Gagal menyimpan lapangan ke database!");
                            case -1 -> showError("Nama lapangan tidak boleh kosong!");
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
            String name   = txtName.getText().trim();
            String type   = txtType.getText().trim();
            String status = (String) cbStatus.getSelectedItem();
            int id = selectedId;

            double price;
            try {
                price = Double.parseDouble(txtPrice.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Harga harus berupa angka!"); return;
            }

            setFormEnabled(false);
            new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() {
                    return controller.updateCourt(id, name, type, price, status);
                }
                @Override
                protected void done() {
                    try {
                        switch (get()) {
                            case  1 -> { showInfo("Data berhasil diupdate!"); clearForm(); loadData(); }
                            case  0 -> showError("Gagal mengupdate lapangan ke database!");
                            case -1 -> showError("Nama lapangan tidak boleh kosong!");
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
                "Hapus lapangan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            int id = selectedId;
            setFormEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return controller.deleteCourt(id);
                }
                @Override
                protected void done() {
                    try {
                        if (get()) { showInfo("Lapangan berhasil dihapus!"); clearForm(); loadData(); }
                        else showError("Gagal menghapus lapangan!");
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

    private void loadData() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("Memuat...");

        new SwingWorker<List<Court>, Void>() {
            @Override
            protected List<Court> doInBackground() {
                return controller.getAllCourts();
            }
            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Court c : get()) {
                        tableModel.addRow(new Object[]{
                            c.getId(), c.getCourtName(), c.getType(),
                            c.getPricePerHour(), c.getStatus()
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
        txtType .setEnabled(enabled);
        txtPrice.setEnabled(enabled);
        cbStatus  .setEnabled(enabled);
        btnAdd    .setEnabled(enabled);
        btnUpdate .setEnabled(enabled);
        btnDelete .setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
    }

    private void clearForm() {
        txtName.setText(""); txtType.setText(""); txtPrice.setText("");
        cbStatus.setSelectedIndex(0);
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