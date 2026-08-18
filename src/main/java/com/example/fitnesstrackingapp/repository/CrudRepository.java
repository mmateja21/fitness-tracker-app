package com.example.fitnesstrackingapp.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//definicija osnovnih crud metoda
public interface CrudRepository<T> {

//cuva novi objekat
    T create (T entity) throws SQLException;

//nalazi objekat prema id
    Optional<T> findById(int id) throws SQLException;

//vraca listu svih objekata
    List<T> findAll() throws SQLException;

//azurira postojece objekte
    boolean update(T entity) throws SQLException;

//brise objekat prema id
    boolean deleteById(int id) throws SQLException;
}
