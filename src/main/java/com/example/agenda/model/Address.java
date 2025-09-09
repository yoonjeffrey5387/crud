package com.example.agenda.model;

import javafx.beans.property.*;
import java.util.Objects;

/**
 * Modelo de Dirección
 * Aplica el principio de Responsabilidad Única (SRP)
 * Encapsula solo los datos de una dirección
 */
public class Address {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty street = new SimpleStringProperty();
    private final StringProperty city = new SimpleStringProperty();
    private final StringProperty state = new SimpleStringProperty();
    private final StringProperty postalCode = new SimpleStringProperty();
    private final StringProperty country = new SimpleStringProperty();

    public Address() {}

    public Address(String street, String city, String state, String postalCode, String country) {
        setStreet(street);
        setCity(city);
        setState(state);
        setPostalCode(postalCode);
        setCountry(country);
    }

    public Address(Integer id, String street, String city, String state, String postalCode, String country) {
        setId(id);
        setStreet(street);
        setCity(city);
        setState(state);
        setPostalCode(postalCode);
        setCountry(country);
    }

    // Getters y Setters para ID
    public Integer getId() { return id.get(); }
    public void setId(Integer value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Getters y Setters para Street
    public String getStreet() { return street.get(); }
    public void setStreet(String value) { street.set(value); }
    public StringProperty streetProperty() { return street; }

    // Getters y Setters para City
    public String getCity() { return city.get(); }
    public void setCity(String value) { city.set(value); }
    public StringProperty cityProperty() { return city; }

    // Getters y Setters para State
    public String getState() { return state.get(); }
    public void setState(String value) { state.set(value); }
    public StringProperty stateProperty() { return state; }

    // Getters y Setters para PostalCode
    public String getPostalCode() { return postalCode.get(); }
    public void setPostalCode(String value) { postalCode.set(value); }
    public StringProperty postalCodeProperty() { return postalCode; }

    // Getters y Setters para Country
    public String getCountry() { return country.get(); }
    public void setCountry(String value) { country.set(value); }
    public StringProperty countryProperty() { return country; }

    /**
     * Obtener dirección completa formateada
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(getStreet());
        
        if (getCity() != null && !getCity().trim().isEmpty()) {
            fullAddress.append(", ").append(getCity());
        }
        
        if (getState() != null && !getState().trim().isEmpty()) {
            fullAddress.append(", ").append(getState());
        }
        
        if (getPostalCode() != null && !getPostalCode().trim().isEmpty()) {
            fullAddress.append(" ").append(getPostalCode());
        }
        
        if (getCountry() != null && !getCountry().trim().isEmpty()) {
            fullAddress.append(", ").append(getCountry());
        }
        
        return fullAddress.toString();
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + getId() +
                ", street='" + getStreet() + '\'' +
                ", city='" + getCity() + '\'' +
                ", state='" + getState() + '\'' +
                ", postalCode='" + getPostalCode() + '\'' +
                ", country='" + getCountry() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getId(), address.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
