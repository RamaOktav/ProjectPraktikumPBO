package controller;

import dao.CourtDAO;
import model.Court;
import java.util.List;

public class CourtController {

    private final CourtDAO dao = new CourtDAO();

    public List<Court> getAllCourts() {
        return dao.getAll();
    }

    /**
     * @return  1  = berhasil
     *          0  = gagal (DB error)
     *         -1  = nama kosong
     */
    public int addCourt(String name, String type, double price, String status) {
        if (name == null || name.trim().isEmpty()) return -1;
        boolean ok = dao.insert(new Court(0, name.trim(), type.trim(), price, status));
        return ok ? 1 : 0;
    }

    /**
     * @return  1  = berhasil
     *          0  = gagal (DB error)
     *         -1  = nama kosong
     */
    public int updateCourt(int id, String name, String type, double price, String status) {
        if (name == null || name.trim().isEmpty()) return -1;
        boolean ok = dao.update(new Court(id, name.trim(), type.trim(), price, status));
        return ok ? 1 : 0;
    }

    public boolean deleteCourt(int id) {
        return dao.delete(id);
    }
}