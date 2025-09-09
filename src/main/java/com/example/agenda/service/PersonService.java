package com.example.agenda.service;

import com.example.agenda.model.Person;
import com.example.agenda.model.Address;
import com.example.agenda.repository.PersonRepository;
import com.example.agenda.repository.AddressRepository;
import com.example.agenda.exception.ServiceException;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para operaciones de negocio de Person
 * Aplica el principio de Responsabilidad Única (SRP)
 * Aplica el principio de Inversión de Dependencias (DIP)
 */
public class PersonService {
    
    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final ValidationService validationService;
    
    /**
     * Constructor con inyección de dependencias
     * Aplica el principio de Inversión de Dependencias (DIP)
     */
    public PersonService(PersonRepository personRepository, 
                        AddressRepository addressRepository,
                        ValidationService validationService) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.validationService = validationService;
    }
    
    /**
     * Crear una nueva persona
     * Aplica el principio de Responsabilidad Única (SRP)
     */
    public Person createPerson(Person person) throws ServiceException {
        validationService.validatePerson(person);
        
        if (person.getEmail() != null && !person.getEmail().isEmpty()) {
            List<Person> existingPersons = personRepository.findByEmail(person.getEmail());
            if (!existingPersons.isEmpty()) {
                throw new ServiceException("Ya existe una persona con este email");
            }
        }
        
        return personRepository.save(person);
    }
    
    /**
     * Obtener persona por ID
     */
    public Optional<Person> getPersonById(Integer id) {
        return personRepository.findById(id);
    }
    
    /**
     * Obtener todas las personas
     */
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    
    /**
     * Buscar personas por nombre
     */
    public List<Person> searchPersonsByName(String name) {
        return personRepository.findByNameContaining(name);
    }
    
    /**
     * Actualizar persona
     */
    public Person updatePerson(Person person) throws ServiceException {
        validationService.validatePerson(person);
        
        if (!personRepository.existsById(person.getId())) {
            throw new ServiceException("La persona no existe");
        }
        
        return personRepository.save(person);
    }
    
    /**
     * Eliminar persona
     */
    public void deletePerson(Integer id) throws ServiceException {
        if (!personRepository.existsById(id)) {
            throw new ServiceException("La persona no existe");
        }
        
        personRepository.deleteById(id);
    }
    
    /**
     * Agregar teléfono a persona
     */
    public void addPhoneToPerson(Integer personId, String phone) throws ServiceException {
        if (!personRepository.existsById(personId)) {
            throw new ServiceException("La persona no existe");
        }
        
        validationService.validatePhone(phone);
        personRepository.addPhoneToPerson(personId, phone);
    }
    
    /**
     * Eliminar teléfono de persona
     */
    public void removePhoneFromPerson(Integer personId, String phone) throws ServiceException {
        if (!personRepository.existsById(personId)) {
            throw new ServiceException("La persona no existe");
        }
        
        personRepository.removePhoneFromPerson(personId, phone);
    }
    
    /**
     * Obtener teléfonos de persona
     */
    public List<String> getPhonesByPersonId(Integer personId) {
        return personRepository.getPhonesByPersonId(personId);
    }
    
    /**
     * Agregar dirección a persona
     */
    public void addAddressToPerson(Integer personId, Address address) throws ServiceException {
        if (!personRepository.existsById(personId)) {
            throw new ServiceException("La persona no existe");
        }
        
        validationService.validateAddress(address);
        
        // Buscar si la dirección ya existe
        Optional<Address> existingAddress = addressRepository.findByFullAddress(
            address.getStreet(), address.getCity(), address.getState(), 
            address.getPostalCode(), address.getCountry()
        );
        
        Integer addressId;
        if (existingAddress.isPresent()) {
            addressId = existingAddress.get().getId();
        } else {
            Address savedAddress = addressRepository.save(address);
            addressId = savedAddress.getId();
        }
        
        personRepository.addAddressToPerson(personId, addressId);
    }
    
    /**
     * Eliminar dirección de persona
     */
    public void removeAddressFromPerson(Integer personId, Integer addressId) throws ServiceException {
        if (!personRepository.existsById(personId)) {
            throw new ServiceException("La persona no existe");
        }
        
        personRepository.removeAddressFromPerson(personId, addressId);
    }
    
    /**
     * Obtener direcciones de persona
     */
    public List<Address> getAddressesByPersonId(Integer personId) {
        List<Integer> addressIds = personRepository.getAddressesByPersonId(personId);
        return addressIds.stream()
                .map(addressRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
