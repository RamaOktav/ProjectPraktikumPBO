// src/interfaces/ICRUDOperations.java
package interfaces;

import java.util.List;

public interface ICRUDOperations<T> {
    boolean insert(T obj);
    List<T> getAll();
    boolean update(T obj);
    boolean delete(int id);
}