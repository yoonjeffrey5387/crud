package com.example.agenda.repository;

import com.example.agenda.model.Address;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz específica para operaciones de Address
 * Aplica el principio de Interface Segregation (ISP)
 */
public interface AddressRepository extends Repository<Address, Integer> {
    List<Address> findByStreet(String street);
    List<Address> findByCity(String city);
    Optional<Address> findByFullAddress(String street, String city, String state, String postalCode, String country);
    List<Address> findSharedAddresses();
}
