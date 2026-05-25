package view;

import controller.CourtController;
import model.Court;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * View for Court management.
 * Delegates all business logic to CourtController.
 */
public class CourtPanel extends JPanel {

    // ─── Controller ────────────────────────────────────────────────
    private final CourtController controller = new CourtController();

    // ─── UI Components ─────────────────────────────────────────────
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField txtName, txtType, txtPrice;
    private final JComboBox<String> cbStatus;
    private int selectedId = -1;

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
        JButton btnAdd     = new JButton("Tambah");
        JButton btnUpdate  = new JButton("Update");
        JButton btnDelete  = new JButton("Hapus");
        JButton btnClear   = new JButton("Bersihkan");
        JButton btnRefresh = new JButton("⟳ Refresh");

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

        // ── Events ──
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

        btnAdd.addActionListener(e -> {
            if (txtName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama lapangan tidak boleh kosong!");
                return;
            }
            try {
                double price = Double.parseDouble(txtPrice.getText());
                boolean ok = controller.addCourt(
                    txtName.getText(), txtType.getText(), price,
                    (String) cbStatus.getSelectedItem());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Lapangan berhasil ditambahkan!");
                    clearForm(); loadData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Pilih data dulu!"); return; }
            try {
                double price = Double.parseDouble(txtPrice.getText());
                boolean ok = controller.updateCourt(
                    selectedId, txtName.getText(), txtType.getText(), price,
                    (String) cbStatus.getSelectedItem());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                    clearForm(); loadData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Pilih data dulu!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus lapangan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.deleteCourt(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Lapangan berhasil dihapus!");
                    clearForm(); loadData();
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
    private void loadData() {
        tableModel.setRowCount(0);
        for (Court c : controller.getAllCourts()) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getCourtName(), c.getType(),
                c.getPricePerHour(), c.getStatus()
            });
        }
    }

    private void clearForm() {
        txtName.setText(""); txtType.setText(""); txtPrice.setText("");
        cbStatus.setSelectedIndex(0);
        selectedId = -1;
        table.clearSelection();
    }
}
