package com.example.agenda;

import com.example.agenda.dao.PersonDao;
import com.example.agenda.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Person> personTable;
    
    @FXML
    private TableColumn<Person, Integer> colId;
    
    @FXML
    private TableColumn<Person, String> colNombre;
    
    @FXML
    private TableColumn<Person, String> colDireccion;
    
    @FXML
    private TableColumn<Person, Integer> colTelefonos;
    
    @FXML
    private Label lblId;
    
    @FXML
    private TextField nombreField;
    
    @FXML
    private TextField direccionField;
    
    @FXML
    private TextField telefonoField;
    
    @FXML
    private ListView<String> telefonosList;
    
    private PersonDao personDao;
    private ObservableList<Person> personList;
    private ObservableList<String> telefonoList;
    private Person selectedPerson;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personDao = new PersonDao();
        personList = FXCollections.observableArrayList();
        telefonoList = FXCollections.observableArrayList();
        
        // Configurar las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefonos.setCellValueFactory(new PropertyValueFactory<>("numTelefonos"));
        
        // Configurar la tabla
        personTable.setItems(personList);
        telefonosList.setItems(telefonoList);
        
        // Listener para selección en la tabla
        personTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadPersonDetails(newValue);
                }
            }
        );
        
        // Cargar datos iniciales
        loadAllPersons();
    }
    
    @FXML
    private void onSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAllPersons();
        } else {
            searchPersons(searchText);
        }
    }
    
    @FXML
    private void onNew() {
        clearFields();
        selectedPerson = null;
        lblId.setText("Nuevo");
    }
    
    @FXML
    private void onSave() {
        try {
            String nombre = nombreField.getText().trim();
            String direccion = direccionField.getText().trim();
            
            if (nombre.isEmpty()) {
                showError("Error de validación", "El nombre es obligatorio");
                return;
            }
            
            if (selectedPerson == null) {
                // Crear nueva persona
                Person newPerson = new Person();
                newPerson.setNombre(nombre);
                newPerson.setDireccion(direccion);
                
                personDao.save(newPerson);
                showInfo("Éxito", "Persona guardada correctamente");
            } else {
                // Actualizar persona existente
                selectedPerson.setNombre(nombre);
                selectedPerson.setDireccion(direccion);
                
                personDao.update(selectedPerson);
                showInfo("Éxito", "Persona actualizada correctamente");
            }
            
            loadAllPersons();
            clearFields();
            
        } catch (SQLException e) {
            showError("Error al guardar", "No se pudo guardar la persona: " + e.getMessage());
        }
    }
    
    @FXML
    private void onDelete() {
        if (selectedPerson == null) {
            showError("Error", "Por favor selecciona una persona para eliminar");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminará la persona: " + selectedPerson.getNombre());
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                personDao.delete(selectedPerson.getId());
                showInfo("Éxito", "Persona eliminada correctamente");
                loadAllPersons();
                clearFields();
            } catch (SQLException e) {
                showError("Error al eliminar", "No se pudo eliminar la persona: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void onRefresh() {
        loadAllPersons();
        clearFields();
    }
    
    @FXML
    private void onAddPhone() {
        String telefono = telefonoField.getText().trim();
        if (!telefono.isEmpty() && selectedPerson != null) {
            try {
                personDao.addTelefono(selectedPerson.getId(), telefono);
                telefonoField.clear();
                loadPersonDetails(selectedPerson); // Recargar teléfonos
                loadAllPersons(); // Actualizar contador en la tabla
                showInfo("Éxito", "Teléfono agregado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo agregar el teléfono: " + e.getMessage());
            }
        } else if (selectedPerson == null) {
            showError("Error", "Primero selecciona o guarda una persona");
        }
    }
    
    @FXML
    private void onRemoveSelectedPhone() {
        String selectedTelefono = telefonosList.getSelectionModel().getSelectedItem();
        if (selectedTelefono != null && selectedPerson != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Eliminar teléfono?");
            alert.setContentText("Se eliminará el teléfono: " + selectedTelefono);
            
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    personDao.removeTelefono(selectedPerson.getId(), selectedTelefono);
                    loadPersonDetails(selectedPerson); // Recargar teléfonos
                    loadAllPersons(); // Actualizar contador en la tabla
                    showInfo("Éxito", "Teléfono eliminado correctamente");
                } catch (SQLException e) {
                    showError("Error", "No se pudo eliminar el teléfono: " + e.getMessage());
                }
            }
        }
    }
    
    private void loadAllPersons() {
        try {
            List<Person> persons = personDao.findAll();
            personList.clear();
            personList.addAll(persons);
        } catch (SQLException e) {
            showError("Error al cargar datos", "No se pudieron cargar las personas: " + e.getMessage());
        }
    }
    
    private void searchPersons(String searchText) {
        try {
            List<Person> persons = personDao.findByName(searchText);
            personList.clear();
            personList.addAll(persons);
        } catch (SQLException e) {
            showError("Error en la búsqueda", "No se pudo realizar la búsqueda: " + e.getMessage());
        }
    }
    
    private void loadPersonDetails(Person person) {
        selectedPerson = person;
        lblId.setText(String.valueOf(person.getId()));
        nombreField.setText(person.getNombre());
        direccionField.setText(person.getDireccion());
        
        // Cargar teléfonos
        try {
            List<String> telefonos = personDao.getTelefonos(person.getId());
            telefonoList.clear();
            telefonoList.addAll(telefonos);
        } catch (SQLException e) {
            showError("Error", "No se pudieron cargar los teléfonos: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        lblId.setText("-");
        nombreField.clear();
        direccionField.clear();
        telefonoField.clear();
        telefonoList.clear();
        selectedPerson = null;
        personTable.getSelectionModel().clearSelection();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
