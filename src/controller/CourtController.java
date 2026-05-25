package controller;

import dao.CourtDAO;
import model.Court;
import java.util.List;

/**
 * Controller for Court management.
 * Mediates between CourtPanel (View) and CourtDAO (Model).
 */
public class CourtController {

    private final CourtDAO dao = new CourtDAO();

    /** Returns all courts from the database. */
    public List<Court> getAllCourts() {
        return dao.getAll();
    }

    /**
     * Validates and adds a new court.
     * @throws NumberFormatException if price is not a valid number (checked in view)
     */
    public boolean addCourt(String name, String type, double price, String status) {
        if (name == null || name.trim().isEmpty()) return false;
        return dao.insert(new Court(0, name.trim(), type.trim(), price, status));
    }

    /** Updates an existing court by ID. */
    public boolean updateCourt(int id, String name, String type, double price, String status) {
        if (name == null || name.trim().isEmpty()) return false;
        return dao.update(new Court(id, name.trim(), type.trim(), price, status));
    }

    /** Deletes a court by ID. */
    public boolean deleteCourt(int id) {
        return dao.delete(id);
    }
}
