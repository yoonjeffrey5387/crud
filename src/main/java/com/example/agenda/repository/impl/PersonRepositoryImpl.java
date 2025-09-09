package com.example.agenda.repository.impl;

import com.example.agenda.Db;
import com.example.agenda.model.Person;
import com.example.agenda.repository.PersonRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Person
 * Aplica el principio de Responsabilidad Única (SRP)
 * Aplica el principio de Inversión de Dependencias (DIP)
 */
public class PersonRepositoryImpl implements PersonRepository {
    
    @Override
    public Optional<Person> findById(Integer id) {
        String sql = "SELECT id, nombre, email FROM Personas WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Person person = new Person(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email")
                    );
                    loadPhonesAndAddresses(con, person);
                    return Optional.of(person);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Person> findAll() {
        String sql = "SELECT id, nombre, email FROM Personas ORDER BY id";
        return executePersonQuery(sql);
    }
    
    @Override
    public List<Person> findByNameContaining(String name) {
        String sql = "SELECT id, nombre, email FROM Personas WHERE nombre LIKE ? ORDER BY id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + name + "%");
            return executePersonQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Person> findByEmail(String email) {
        String sql = "SELECT id, nombre, email FROM Personas WHERE email = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);
            return executePersonQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Person save(Person person) {
        if (person.getId() == null || person.getId() == 0) {
            return insert(person);
        } else {
            return update(person);
        }
    }
    
    private Person insert(Person person) {
        String sql = "INSERT INTO Personas (nombre, email) VALUES (?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, person.getNombre());
            ps.setString(2, person.getEmail());
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    person.setId(keys.getInt(1));
                }
            }
            
            // Guardar teléfonos
            savePhones(con, person);
            
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar persona", e);
        }
    }
    
    private Person update(Person person) {
        String sql = "UPDATE Personas SET nombre = ?, email = ? WHERE id = ?";
        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, person.getNombre());
                ps.setString(2, person.getEmail());
                ps.setInt(3, person.getId());
                ps.executeUpdate();
            }
            
            // Actualizar teléfonos
            updatePhones(con, person);
            
            con.commit();
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar persona", e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Personas WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar persona", e);
        }
    }
    
    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM Personas WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void addPhoneToPerson(Integer personId, String phone) {
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            ps.setString(2, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al agregar teléfono", e);
        }
    }
    
    @Override
    public void removePhoneFromPerson(Integer personId, String phone) {
        String sql = "DELETE FROM Telefonos WHERE personaId = ? AND telefono = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            ps.setString(2, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar teléfono", e);
        }
    }
    
    @Override
    public List<String> getPhonesByPersonId(Integer personId) {
        String sql = "SELECT telefono FROM Telefonos WHERE personaId = ? ORDER BY id";
        List<String> phones = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    phones.add(rs.getString("telefono"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phones;
    }
    
    @Override
    public void addAddressToPerson(Integer personId, Integer addressId) {
        String sql = "INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            ps.setInt(2, addressId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al agregar dirección", e);
        }
    }
    
    @Override
    public void removeAddressFromPerson(Integer personId, Integer addressId) {
        String sql = "DELETE FROM PersonaDirecciones WHERE personaId = ? AND direccionId = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            ps.setInt(2, addressId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar dirección", e);
        }
    }
    
    @Override
    public List<Integer> getAddressesByPersonId(Integer personId) {
        String sql = "SELECT direccionId FROM PersonaDirecciones WHERE personaId = ?";
        List<Integer> addressIds = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, personId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addressIds.add(rs.getInt("direccionId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addressIds;
    }
    
    // Métodos auxiliares privados
    private List<Person> executePersonQuery(String sql) {
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return executePersonQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private List<Person> executePersonQuery(PreparedStatement ps) throws SQLException {
        List<Person> persons = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Person person = new Person(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email")
                );
                loadPhonesAndAddresses(ps.getConnection(), person);
                persons.add(person);
            }
        }
        return persons;
    }
    
    private void loadPhonesAndAddresses(Connection con, Person person) {
        // Cargar teléfonos
        List<String> phones = getPhonesByPersonId(person.getId());
        person.setTelefonos(phones);
        
        // Cargar direcciones
        List<Integer> addressIds = getAddressesByPersonId(person.getId());
        // Las direcciones se cargarán en el servicio cuando sea necesario
    }
    
    private void savePhones(Connection con, Person person) throws SQLException {
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String phone : person.getTelefonos()) {
                ps.setInt(1, person.getId());
                ps.setString(2, phone);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    private void updatePhones(Connection con, Person person) throws SQLException {
        // Eliminar teléfonos existentes
        String deleteSql = "DELETE FROM Telefonos WHERE personaId = ?";
        try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
            ps.setInt(1, person.getId());
            ps.executeUpdate();
        }
        
        // Insertar nuevos teléfonos
        savePhones(con, person);
    }
}
