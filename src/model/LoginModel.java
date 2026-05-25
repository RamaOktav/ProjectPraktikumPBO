package model;

import dao.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginModel {

    Connection conn;

    public LoginModel() {
        conn = DBConnection.getConnection();
    }

    public boolean cekLogin(String username, String password) {

        try {

            String query = "SELECT * FROM admin WHERE nama=? AND password=?";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            return result.next();

        } catch (Exception e) {

            System.out.println("Error Login : " + e.getMessage());

            return false;
        }
    }
}