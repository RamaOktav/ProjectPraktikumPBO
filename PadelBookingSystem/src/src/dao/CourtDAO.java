// src/dao/CourtDAO.java
package dao;

import interfaces.ICRUDOperations;
import model.Court;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourtDAO implements ICRUDOperations<Court> {

    @Override
    public boolean insert(Court c) {
        String sql = "INSERT INTO tb_courts (court_name, type, price_per_hour, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourtName());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPricePerHour());
            ps.setString(4, c.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Court> getAll() {
        List<Court> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_courts ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Court(rs.getInt("id"), rs.getString("court_name"),
                    rs.getString("type"), rs.getDouble("price_per_hour"),
                    rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean update(Court c) {
        String sql = "UPDATE tb_courts SET court_name=?, type=?, price_per_hour=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourtName());
            ps.setString(2, c.getType());
            ps.setDouble(3, c.getPricePerHour());
            ps.setString(4, c.getStatus());
            ps.setInt(5, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tb_courts WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Court> getAvailable() {
        List<Court> list = new ArrayList<>();
        String sql = "SELECT * FROM tb_courts WHERE status='Available'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Court(rs.getInt("id"), rs.getString("court_name"),
                    rs.getString("type"), rs.getDouble("price_per_hour"),
                    rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}