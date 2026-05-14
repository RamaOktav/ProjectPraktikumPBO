package view;

import dao.CourtDAO;
import model.Court;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CourtPanel extends JPanel {
    private CourtDAO dao = new CourtDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtType, txtPrice;
    private JComboBox<String> cbStatus;
    private int selectedId = -1;

    public CourtPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form input
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Lapangan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName  = new JTextField(20);
        txtType  = new JTextField(20);
        txtPrice = new JTextField(20);
        cbStatus = new JComboBox<>(new String[]{"Available", "Unavailable"});

        String[] labels = {"Nama Lapangan:", "Tipe:", "Harga/Jam (Rp):", "Status:"};
        JComponent[] fields = {txtName, txtType, txtPrice, cbStatus};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
        }

        // Tombol CRUD
        JButton btnAdd    = new JButton("Tambah");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Hapus");
        JButton btnClear  = new JButton("Bersihkan");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        // Tabel
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama Lapangan", "Tipe", "Harga/Jam", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        // Klik baris tabel → isi form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtType.setText((String) tableModel.getValueAt(row, 2));
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
                Court c = new Court(0, txtName.getText(), txtType.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    (String) cbStatus.getSelectedItem());
                if (dao.insert(c)) {
                    JOptionPane.showMessageDialog(this, "Lapangan berhasil ditambahkan!");
                    clearForm(); loadData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
                return;
            }
            try {
                Court c = new Court(selectedId, txtName.getText(), txtType.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    (String) cbStatus.getSelectedItem());
                if (dao.update(c)) {
                    JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                    clearForm(); loadData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka!");
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId < 0) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus lapangan ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Lapangan berhasil dihapus!");
                    clearForm(); loadData();
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Court c : dao.getAll()) {
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