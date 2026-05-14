// src/view/CustomerPanel.java
package view;

import dao.CustomerDAO;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private CustomerDAO dao = new CustomerDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtName, txtPhone, txtEmail;
    private int selectedId = -1;

    public CustomerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form input
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data Pelanggan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName  = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);

        String[] labels = {"Nama:", "Telepon:", "Email:"};
        JTextField[] fields = {txtName, txtPhone, txtEmail};
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

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        // Tabel
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama", "Telepon", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        // Event
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtPhone.setText((String) tableModel.getValueAt(row, 2));
                txtEmail.setText((String) tableModel.getValueAt(row, 3));
            }
        });

        btnAdd.addActionListener(e -> {
            if (txtName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!");
                return;
            }
            Customer c = new Customer(0, txtName.getText(), txtPhone.getText(), txtEmail.getText());
            if (dao.insert(c)) {
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan!");
                clearForm(); loadData();
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Pilih data dulu!"); return; }
            Customer c = new Customer(selectedId, txtName.getText(), txtPhone.getText(), txtEmail.getText());
            if (dao.update(c)) {
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                clearForm(); loadData();
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Pilih data dulu!"); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(selectedId)) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                    clearForm(); loadData();
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Customer c : dao.getAll()) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail()});
        }
    }

    private void clearForm() {
        txtName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        selectedId = -1; table.clearSelection();
    }
}