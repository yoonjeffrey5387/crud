package com.example.agenda.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones CRUD
 * Aplica el principio de Interface Segregation (ISP)
 */
public interface Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);
    void deleteById(ID id);
    boolean existsById(ID id);
}
