package com.example.agenda.dao;

import com.example.agenda.Db;
import com.example.agenda.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDao {

    public ObservableList<Person> findAll(String nameLike) throws SQLException {
        String sql = "SELECT p.id, p.nombre, p.direccion FROM Personas p "
                   + (nameLike != null && !nameLike.isBlank() ? "WHERE p.nombre LIKE ? " : "")
                   + "ORDER BY p.id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (nameLike != null && !nameLike.isBlank()) {
                ps.setString(1, "%" + nameLike + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                ObservableList<Person> list = FXCollections.observableArrayList();
                while (rs.next()) {
                    Person p = new Person(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                    p.getTelefonos().setAll(loadPhones(con, p.getId()));
                    list.add(p);
                }
                return list;
            }
        }
    }

    private List<String> loadPhones(Connection con, int personId) throws SQLException {
        String sql = "SELECT telefono FROM Telefonos WHERE personaId = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personId);
            List<String> phones = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) phones.add(rs.getString(1));
            }
            return phones;
        }
    }

    public Person insert(Person p) throws SQLException {
        String sqlP = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String sqlT = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlP, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getDireccion());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setId(keys.getInt(1));
                }
            }
            try (PreparedStatement psT = con.prepareStatement(sqlT)) {
                for (String t : p.getTelefonos()) {
                    psT.setInt(1, p.getId());
                    psT.setString(2, t);
                    psT.addBatch();
                }
                psT.executeBatch();
            }
            con.commit();
            return p;
        }
    }

    public void update(Person p) throws SQLException {
        String sqlP = "UPDATE Personas SET nombre=?, direccion=? WHERE id=?";
        String sqlDelT = "DELETE FROM Telefonos WHERE personaId=?";
        String sqlInsT = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlP)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getDireccion());
                ps.setInt(3, p.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement psDel = con.prepareStatement(sqlDelT)) {
                psDel.setInt(1, p.getId());
                psDel.executeUpdate();
            }
            try (PreparedStatement psIns = con.prepareStatement(sqlInsT)) {
                for (String t : p.getTelefonos()) {
                    psIns.setInt(1, p.getId());
                    psIns.setString(2, t);
                    psIns.addBatch();
                }
                psIns.executeBatch();
            }
            con.commit();
        }
    }

    public void delete(int personId) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.executeUpdate();
        }
    }

    // Additional methods needed by MainController
    
    public void save(Person p) throws SQLException {
        if (p.getId() == 0) {
            insert(p);
        } else {
            update(p);
        }
    }
    
    public List<Person> findAll() throws SQLException {
        return new ArrayList<>(findAll(null));
    }
    
    public List<Person> findByName(String name) throws SQLException {
        return new ArrayList<>(findAll(name));
    }
    
    public List<String> getTelefonos(int personId) throws SQLException {
        try (Connection con = Db.getConnection()) {
            return loadPhones(con, personId);
        }
    }
    
    public void addTelefono(int personId, String telefono) throws SQLException {
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.setString(2, telefono);
            ps.executeUpdate();
        }
    }
    
    public void removeTelefono(int personId, String telefono) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE personaId = ? AND telefono = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.setString(2, telefono);
            ps.executeUpdate();
        }
    }
}
