package com.example.agenda.repository.impl;

import com.example.agenda.Db;
import com.example.agenda.model.Address;
import com.example.agenda.repository.AddressRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Address
 * Aplica el principio de Responsabilidad Única (SRP)
 * Aplica el principio de Inversión de Dependencias (DIP)
 */
public class AddressRepositoryImpl implements AddressRepository {
    
    @Override
    public Optional<Address> findById(Integer id) {
        String sql = "SELECT id, calle, ciudad, estado, codigoPostal, pais FROM Direcciones WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createAddressFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Address> findAll() {
        String sql = "SELECT id, calle, ciudad, estado, codigoPostal, pais FROM Direcciones ORDER BY id";
        return executeAddressQuery(sql);
    }
    
    @Override
    public List<Address> findByStreet(String street) {
        String sql = "SELECT id, calle, ciudad, estado, codigoPostal, pais FROM Direcciones WHERE calle LIKE ? ORDER BY id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + street + "%");
            return executeAddressQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Address> findByCity(String city) {
        String sql = "SELECT id, calle, ciudad, estado, codigoPostal, pais FROM Direcciones WHERE ciudad LIKE ? ORDER BY id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + city + "%");
            return executeAddressQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Optional<Address> findByFullAddress(String street, String city, String state, String postalCode, String country) {
        String sql = "SELECT id, calle, ciudad, estado, codigoPostal, pais FROM Direcciones " +
                    "WHERE calle = ? AND ciudad = ? AND estado = ? AND codigoPostal = ? AND pais = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, street);
            ps.setString(2, city);
            ps.setString(3, state);
            ps.setString(4, postalCode);
            ps.setString(5, country);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createAddressFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Address> findSharedAddresses() {
        String sql = "SELECT d.id, d.calle, d.ciudad, d.estado, d.codigoPostal, d.pais " +
                    "FROM Direcciones d " +
                    "INNER JOIN PersonaDirecciones pd ON d.id = pd.direccionId " +
                    "GROUP BY d.id, d.calle, d.ciudad, d.estado, d.codigoPostal, d.pais " +
                    "HAVING COUNT(pd.personaId) > 1 " +
                    "ORDER BY d.id";
        return executeAddressQuery(sql);
    }
    
    @Override
    public Address save(Address address) {
        if (address.getId() == null || address.getId() == 0) {
            return insert(address);
        } else {
            return update(address);
        }
    }
    
    private Address insert(Address address) {
        String sql = "INSERT INTO Direcciones (calle, ciudad, estado, codigoPostal, pais) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, address.getStreet());
            ps.setString(2, address.getCity());
            ps.setString(3, address.getState());
            ps.setString(4, address.getPostalCode());
            ps.setString(5, address.getCountry());
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    address.setId(keys.getInt(1));
                }
            }
            
            return address;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar dirección", e);
        }
    }
    
    private Address update(Address address) {
        String sql = "UPDATE Direcciones SET calle = ?, ciudad = ?, estado = ?, codigoPostal = ?, pais = ? WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, address.getStreet());
            ps.setString(2, address.getCity());
            ps.setString(3, address.getState());
            ps.setString(4, address.getPostalCode());
            ps.setString(5, address.getCountry());
            ps.setInt(6, address.getId());
            ps.executeUpdate();
            
            return address;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar dirección", e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM Direcciones WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar dirección", e);
        }
    }
    
    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM Direcciones WHERE id = ?";
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
    
    // Métodos auxiliares privados
    private List<Address> executeAddressQuery(String sql) {
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return executeAddressQuery(ps);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private List<Address> executeAddressQuery(PreparedStatement ps) throws SQLException {
        List<Address> addresses = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                addresses.add(createAddressFromResultSet(rs));
            }
        }
        return addresses;
    }
    
    private Address createAddressFromResultSet(ResultSet rs) throws SQLException {
        return new Address(
            rs.getInt("id"),
            rs.getString("calle"),
            rs.getString("ciudad"),
            rs.getString("estado"),
            rs.getString("codigoPostal"),
            rs.getString("pais")
        );
    }
}
