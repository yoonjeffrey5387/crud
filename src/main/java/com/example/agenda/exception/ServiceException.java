package com.example.agenda.exception;

/**
 * Excepción personalizada para errores de servicio
 * Aplica el principio de Responsabilidad Única (SRP)
 */
public class ServiceException extends Exception {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
