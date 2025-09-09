package com.example.agenda.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * Modelo de Persona
 * Aplica el principio de Responsabilidad Única (SRP)
 * Encapsula solo los datos de una persona
 */
public class Person {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObservableList<String> telefonos = FXCollections.observableArrayList();
    private final ObservableList<Address> direcciones = FXCollections.observableArrayList();

    public Person() {}

    public Person(String nombre, String email) {
        setNombre(nombre);
        setEmail(email);
    }

    public Person(Integer id, String nombre, String email) {
        setId(id);
        setNombre(nombre);
        setEmail(email);
    }

    // Getters y Setters para ID
    public Integer getId() { return id.get(); }
    public void setId(Integer value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // Getters y Setters para Nombre
    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value); }
    public StringProperty nombreProperty() { return nombre; }

    // Getters y Setters para Email
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    // Gestión de teléfonos
    public ObservableList<String> getTelefonos() { return telefonos; }
    public void setTelefonos(List<String> telefonos) {
        this.telefonos.clear();
        this.telefonos.addAll(telefonos);
    }
    public int getNumTelefonos() { return telefonos.size(); }

    // Gestión de direcciones
    public ObservableList<Address> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Address> direcciones) {
        this.direcciones.clear();
        this.direcciones.addAll(direcciones);
    }
    public int getNumDirecciones() { return direcciones.size(); }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telefonos=" + getNumTelefonos() +
                ", direcciones=" + getNumDirecciones() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getId() != null && getId().equals(person.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}