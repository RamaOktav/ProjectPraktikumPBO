package controller;

import model.LoginModel;
import view.LoginPage;
import view.MainFrame;
import javax.swing.*;

public class ControllerLogin {

    private final LoginPage   view;
    private final LoginModel  model;

    public ControllerLogin(LoginPage view) {
        this.view  = view;
        this.model = new LoginModel();
    }

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Username dan Password tidak boleh kosong!");
            return;
        }

        
        view.setFormEnabled(false);
        view.setStatus("Memeriksa...");

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() {
                // Query DB jalan di background thread — EDT bebas
                return model.cekLogin(username, password);
            }

            @Override
            protected void done() {
               
                try {
                    boolean berhasil = get();
                    if (berhasil) {
                        JOptionPane.showMessageDialog(null, "Login Berhasil!");
                        view.dispose();
                        new MainFrame(username);
                    } else {
                        JOptionPane.showMessageDialog(null,
                            "Username atau Password salah!");
                        view.setFormEnabled(true);
                        view.setStatus("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan koneksi!");
                    view.setFormEnabled(true);
                    view.setStatus("");
                }
            }
        }.execute();
    }
}