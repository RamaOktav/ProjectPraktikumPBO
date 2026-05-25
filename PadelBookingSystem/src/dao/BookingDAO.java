// src/dao/BookingDAO.java
package dao;

import interfaces.ICRUDOperations;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements ICRUDOperations<Booking> {

    @Override
    public boolean insert(Booking b) {
        String sql = "INSERT INTO tb_bookings (customer_id, court_id, booking_date, " +
                     "start_time, end_time, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getCourtId());
            ps.setString(3, b.getBookingDate());
            ps.setString(4, b.getStartTime());
            ps.setString(5, b.getEndTime());
            ps.setDouble(6, b.getTotalPrice());
            ps.setString(7, b.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS customer_name, co.court_name " +
                     "FROM tb_bookings b " +
                     "JOIN tb_customers c  ON b.customer_id = c.id " +
                     "JOIN tb_courts    co ON b.court_id    = co.id " +
                     "ORDER BY b.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Booking bk = new Booking(
                    rs.getInt("id"), rs.getInt("customer_id"),
                    rs.getInt("court_id"), rs.getString("booking_date"),
                    rs.getString("start_time"), rs.getString("end_time"),
                    rs.getDouble("total_price"), rs.getString("status"));
                bk.setCustomerName(rs.getString("customer_name"));
                bk.setCourtName(rs.getString("court_name"));
                list.add(bk);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean update(Booking b) {
        String sql = "UPDATE tb_bookings SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getStatus());
            ps.setInt(2, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tb_bookings WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Checks whether a court is already booked (non-Cancelled) on a given date
     * with an overlapping time slot.
     * Overlap condition: existing.start < newEnd  AND  existing.end > newStart
     *
     * @param excludeBookingId  pass 0 (or -1) when creating; pass the booking's own id when editing
     */
    public boolean isConflict(int courtId, String date,
                               String startTime, String endTime,
                               int excludeBookingId) {
        String sql = "SELECT COUNT(*) FROM tb_bookings " +
                     "WHERE court_id = ? AND booking_date = ? AND status != 'Cancelled' " +
                     "AND id != ? " +
                     "AND start_time < ? AND end_time > ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courtId);
            ps.setString(2, date);
            ps.setInt(3, excludeBookingId);
            ps.setString(4, endTime);
            ps.setString(5, startTime);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
