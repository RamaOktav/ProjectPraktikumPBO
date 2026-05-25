package controller;

import model.LoginModel;
import view.LoginPage;
import view.MainFrame;
import javax.swing.*;

/**
 * Controller for Login.
 * Mediates between LoginPage (View) and LoginModel (Model).
 */
public class ControllerLogin {

    private final LoginPage view;
    private final LoginModel model;

    public ControllerLogin(LoginPage view) {
        this.view = view;
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

        if (model.cekLogin(username, password)) {
            JOptionPane.showMessageDialog(null, "Login Berhasil!");
            view.dispose();
            new MainFrame(username);
        } else {
            JOptionPane.showMessageDialog(null,
                "Username atau Password salah!");
        }
    }
}
