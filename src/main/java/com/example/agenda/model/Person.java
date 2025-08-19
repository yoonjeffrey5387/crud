package com.example.agenda.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Person {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty direccion = new SimpleStringProperty();
    private final ObservableList<String> telefonos = FXCollections.observableArrayList();

    public Person(){}

    public Person(int id, String nombre, String direccion) {
        setId(id);
        setNombre(nombre);
        setDireccion(direccion);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value); }
    public StringProperty nombreProperty() { return nombre; }

    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String value) { direccion.set(value); }
    public StringProperty direccionProperty() { return direccion; }

    public ObservableList<String> getTelefonos() { return telefonos; }
    
    public int getNumTelefonos() { return telefonos.size(); }
}
