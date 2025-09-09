package com.example.agenda.repository;

import com.example.agenda.model.Person;
import java.util.List;

/**
 * Interfaz específica para operaciones de Person
 * Aplica el principio de Interface Segregation (ISP)
 */
public interface PersonRepository extends Repository<Person, Integer> {
    List<Person> findByNameContaining(String name);
    List<Person> findByEmail(String email);
    void addPhoneToPerson(Integer personId, String phone);
    void removePhoneFromPerson(Integer personId, String phone);
    List<String> getPhonesByPersonId(Integer personId);
    void addAddressToPerson(Integer personId, Integer addressId);
    void removeAddressFromPerson(Integer personId, Integer addressId);
    List<Integer> getAddressesByPersonId(Integer personId);
}
