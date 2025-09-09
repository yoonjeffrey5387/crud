package com.example.agenda.service;

import com.example.agenda.model.Person;
import com.example.agenda.model.Address;
import com.example.agenda.exception.ServiceException;
import java.util.regex.Pattern;

/**
 * Servicio de validación
 * Aplica el principio de Responsabilidad Única (SRP)
 * Separado para mantener alta cohesión
 */
public class ValidationService {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-\\(\\)]{7,15}$"
    );
    
    /**
     * Validar persona
     */
    public void validatePerson(Person person) throws ServiceException {
        if (person == null) {
            throw new ServiceException("La persona no puede ser nula");
        }
        
        if (person.getNombre() == null || person.getNombre().trim().isEmpty()) {
            throw new ServiceException("El nombre es obligatorio");
        }
        
        if (person.getNombre().length() > 100) {
            throw new ServiceException("El nombre no puede exceder 100 caracteres");
        }
        
        if (person.getEmail() != null && !person.getEmail().trim().isEmpty()) {
            validateEmail(person.getEmail());
        }
    }
    
    /**
     * Validar email
     */
    public void validateEmail(String email) throws ServiceException {
        if (email == null || email.trim().isEmpty()) {
            return; // Email es opcional
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ServiceException("Formato de email inválido");
        }
        
        if (email.length() > 100) {
            throw new ServiceException("El email no puede exceder 100 caracteres");
        }
    }
    
    /**
     * Validar teléfono
     */
    public void validatePhone(String phone) throws ServiceException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ServiceException("El teléfono es obligatorio");
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ServiceException("Formato de teléfono inválido");
        }
        
        if (phone.length() > 20) {
            throw new ServiceException("El teléfono no puede exceder 20 caracteres");
        }
    }
    
    /**
     * Validar dirección
     */
    public void validateAddress(Address address) throws ServiceException {
        if (address == null) {
            throw new ServiceException("La dirección no puede ser nula");
        }
        
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new ServiceException("La calle es obligatoria");
        }
        
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new ServiceException("La ciudad es obligatoria");
        }
        
        if (address.getStreet().length() > 200) {
            throw new ServiceException("La calle no puede exceder 200 caracteres");
        }
        
        if (address.getCity().length() > 100) {
            throw new ServiceException("La ciudad no puede exceder 100 caracteres");
        }
    }
}
