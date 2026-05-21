package controller;

import model.LoginModel;
import view.LoginPage;
import view.MainFrame;

import javax.swing.*;

public class ControllerLogin {

    LoginPage view;
    LoginModel model;

    public ControllerLogin(LoginPage view) {
        this.view = view;
        model = new LoginModel();
    }

    public void login() {

        String username = view.getUsername();
        String password = view.getPassword();

        // VALIDASI FIELD KOSONG
        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Username dan Password tidak boleh kosong!"
            );

            return;
        }

        // CEK LOGIN KE DATABASE
        boolean cekLogin = model.cekLogin(username, password);

        if (cekLogin) {

            JOptionPane.showMessageDialog(
                    null,
                    "Login Berhasil!"
            );

            // PINDAH KE MENU
            view.dispose();

            new MainFrame(username);

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Username atau Password salah!"
            );
        }
    }
}