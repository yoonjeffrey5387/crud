package com.example.agenda;

import com.example.agenda.model.Person;
import com.example.agenda.model.Address;
import com.example.agenda.service.PersonService;
import com.example.agenda.service.ValidationService;
import com.example.agenda.repository.PersonRepository;
import com.example.agenda.repository.AddressRepository;
import com.example.agenda.repository.impl.PersonRepositoryImpl;
import com.example.agenda.repository.impl.AddressRepositoryImpl;
import com.example.agenda.exception.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación
 * Aplica el principio de Responsabilidad Única (SRP)
 * Aplica el principio de Inversión de Dependencias (DIP)
 */
public class MainController implements Initializable {
    
    // Componentes de búsqueda
    @FXML private TextField searchField;
    
    // Tabla de personas
    @FXML private TableView<Person> personTable;
    @FXML private TableColumn<Person, Integer> colId;
    @FXML private TableColumn<Person, String> colNombre;
    @FXML private TableColumn<Person, String> colEmail;
    @FXML private TableColumn<Person, Integer> colTelefonos;
    @FXML private TableColumn<Person, Integer> colDirecciones;
    
    // Formulario de persona
    @FXML private Label lblId;
    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    
    // Gestión de teléfonos
    @FXML private TextField telefonoField;
    @FXML private ListView<String> telefonosList;
    
    // Gestión de direcciones
    @FXML private ListView<Address> direccionesList;
    @FXML private TextField calleField;
    @FXML private TextField ciudadField;
    @FXML private TextField estadoField;
    @FXML private TextField codigoPostalField;
    @FXML private TextField paisField;
    
    // Servicios y datos
    private PersonService personService;
    private ObservableList<Person> personList;
    private ObservableList<String> telefonoList;
    private ObservableList<Address> direccionList;
    private Person selectedPerson;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeServices();
        configureTable();
        configureLists();
        setupEventHandlers();
        loadInitialData();
    }
    
    /**
     * Inicializar servicios con inyección de dependencias
     * Aplica el principio de Inversión de Dependencias (DIP)
     */
    private void initializeServices() {
        PersonRepository personRepository = new PersonRepositoryImpl();
        AddressRepository addressRepository = new AddressRepositoryImpl();
        ValidationService validationService = new ValidationService();
        
        personService = new PersonService(personRepository, addressRepository, validationService);
        
        personList = FXCollections.observableArrayList();
        telefonoList = FXCollections.observableArrayList();
        direccionList = FXCollections.observableArrayList();
    }
    
    /**
     * Configurar tabla de personas
     * Aplica el principio de Responsabilidad Única (SRP)
     */
    private void configureTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefonos.setCellValueFactory(new PropertyValueFactory<>("numTelefonos"));
        colDirecciones.setCellValueFactory(new PropertyValueFactory<>("numDirecciones"));
        personTable.setItems(personList);
    }
    
    /**
     * Configurar listas
     * Aplica el principio de Responsabilidad Única (SRP)
     */
    private void configureLists() {
        telefonosList.setItems(telefonoList);
        direccionesList.setItems(direccionList);
        
        // Configurar ListView de direcciones para mostrar texto personalizado
        direccionesList.setCellFactory(listView -> new ListCell<Address>() {
            @Override
            protected void updateItem(Address address, boolean empty) {
                super.updateItem(address, empty);
                setText(empty || address == null ? null : address.getFullAddress());
            }
        });
    }
    
    /**
     * Configurar manejadores de eventos
     * Aplica el principio de Responsabilidad Única (SRP)
     */
    private void setupEventHandlers() {
        // Listener para selección en la tabla
        personTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadPersonDetails(newValue);
                }
            }
        );
        
        // Enter en campo de búsqueda
        searchField.setOnAction(e -> onSearch());
    }
    
    /**
     * Cargar datos iniciales
     * Aplica el principio de Responsabilidad Única (SRP)
     */
    private void loadInitialData() {
        loadAllPersons();
    }
    
    // ========== MÉTODOS DE BÚSQUEDA ==========
    
    @FXML
    private void onSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAllPersons();
        } else {
            searchPersons(searchText);
        }
    }
    
    // ========== MÉTODOS CRUD DE PERSONAS ==========
    
    @FXML
    private void onNew() {
        clearAllFields();
        selectedPerson = null;
        lblId.setText("Nuevo");
    }
    
    @FXML
    private void onSave() {
        if (!validatePersonForm()) return;
        
        try {
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            
            if (selectedPerson == null) {
                // Crear nueva persona
                Person newPerson = new Person(nombre, email);
                personService.createPerson(newPerson);
                showMessage("Éxito", "Persona creada correctamente", Alert.AlertType.INFORMATION);
            } else {
                // Actualizar persona existente
                selectedPerson.setNombre(nombre);
                selectedPerson.setEmail(email);
                personService.updatePerson(selectedPerson);
                showMessage("Éxito", "Persona actualizada correctamente", Alert.AlertType.INFORMATION);
            }
            
            loadAllPersons();
            clearAllFields();
            
        } catch (ServiceException e) {
            showMessage("Error al guardar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void onDelete() {
        if (selectedPerson == null) {
            showMessage("Error", "Selecciona una persona para eliminar", Alert.AlertType.ERROR);
            return;
        }
        
        if (confirmAction("Eliminar persona", 
                "¿Estás seguro de eliminar a " + selectedPerson.getNombre() + "?")) {
            try {
                personService.deletePerson(selectedPerson.getId());
                showMessage("Éxito", "Persona eliminada correctamente", Alert.AlertType.INFORMATION);
                loadAllPersons();
                clearAllFields();
            } catch (ServiceException e) {
                showMessage("Error al eliminar", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void onRefresh() {
        loadAllPersons();
        clearAllFields();
    }
    
    // ========== MÉTODOS DE TELÉFONOS ==========
    
    @FXML
    private void onAddPhone() {
        if (selectedPerson == null) {
            showMessage("Error", "Primero selecciona una persona", Alert.AlertType.ERROR);
            return;
        }
        
        String telefono = telefonoField.getText().trim();
        if (telefono.isEmpty()) {
            showMessage("Error", "Ingresa un número de teléfono", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            personService.addPhoneToPerson(selectedPerson.getId(), telefono);
            telefonoField.clear();
            refreshPersonData();
            showMessage("Éxito", "Teléfono agregado correctamente", Alert.AlertType.INFORMATION);
        } catch (ServiceException e) {
            showMessage("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void onRemovePhone() {
        String selectedTelefono = telefonosList.getSelectionModel().getSelectedItem();
        if (selectedTelefono == null) {
            showMessage("Error", "Selecciona un teléfono para eliminar", Alert.AlertType.ERROR);
            return;
        }
        
        if (confirmAction("Eliminar teléfono", 
                "¿Eliminar el teléfono " + selectedTelefono + "?")) {
            try {
                personService.removePhoneFromPerson(selectedPerson.getId(), selectedTelefono);
                refreshPersonData();
                showMessage("Éxito", "Teléfono eliminado correctamente", Alert.AlertType.INFORMATION);
            } catch (ServiceException e) {
                showMessage("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    // ========== MÉTODOS DE DIRECCIONES ==========
    
    @FXML
    private void onAddAddress() {
        if (selectedPerson == null) {
            showMessage("Error", "Primero selecciona una persona", Alert.AlertType.ERROR);
            return;
        }
        
        if (!validateAddressForm()) return;
        
        try {
            Address newAddress = new Address(
                calleField.getText().trim(),
                ciudadField.getText().trim(),
                estadoField.getText().trim(),
                codigoPostalField.getText().trim(),
                paisField.getText().trim()
            );
            
            personService.addAddressToPerson(selectedPerson.getId(), newAddress);
            clearAddressFields();
            refreshPersonData();
            showMessage("Éxito", "Dirección agregada correctamente", Alert.AlertType.INFORMATION);
        } catch (ServiceException e) {
            showMessage("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void onRemoveAddress() {
        Address selectedAddress = direccionesList.getSelectionModel().getSelectedItem();
        if (selectedAddress == null) {
            showMessage("Error", "Selecciona una dirección para eliminar", Alert.AlertType.ERROR);
            return;
        }
        
        if (confirmAction("Eliminar dirección", 
                "¿Eliminar la dirección " + selectedAddress.getFullAddress() + "?")) {
            try {
                personService.removeAddressFromPerson(selectedPerson.getId(), selectedAddress.getId());
                refreshPersonData();
                showMessage("Éxito", "Dirección eliminada correctamente", Alert.AlertType.INFORMATION);
            } catch (ServiceException e) {
                showMessage("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    private void loadAllPersons() {
        try {
            List<Person> persons = personService.getAllPersons();
            personList.clear();
            personList.addAll(persons);
        } catch (Exception e) {
            showMessage("Error al cargar datos", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void searchPersons(String searchText) {
        try {
            List<Person> persons = personService.searchPersonsByName(searchText);
            personList.clear();
            personList.addAll(persons);
        } catch (Exception e) {
            showMessage("Error en la búsqueda", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void loadPersonDetails(Person person) {
        selectedPerson = person;
        lblId.setText(String.valueOf(person.getId()));
        nombreField.setText(person.getNombre());
        emailField.setText(person.getEmail());
        
        // Cargar teléfonos y direcciones
        telefonoList.clear();
        telefonoList.addAll(person.getTelefonos());
        
        direccionList.clear();
        direccionList.addAll(person.getDirecciones());
    }
    
    private void refreshPersonData() {
        if (selectedPerson != null) {
            try {
                Person updatedPerson = personService.getPersonById(selectedPerson.getId()).orElse(null);
                if (updatedPerson != null) {
                    loadPersonDetails(updatedPerson);
                }
                loadAllPersons(); // Actualizar contadores en la tabla
            } catch (Exception e) {
                showMessage("Error al actualizar", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void clearAllFields() {
        lblId.setText("-");
        nombreField.clear();
        emailField.clear();
        telefonoField.clear();
        telefonoList.clear();
        direccionList.clear();
        clearAddressFields();
        selectedPerson = null;
        personTable.getSelectionModel().clearSelection();
    }
    
    private void clearAddressFields() {
        calleField.clear();
        ciudadField.clear();
        estadoField.clear();
        codigoPostalField.clear();
        paisField.clear();
    }
    
    private boolean validatePersonForm() {
        if (nombreField.getText().trim().isEmpty()) {
            showMessage("Error de validación", "El nombre es obligatorio", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private boolean validateAddressForm() {
        if (calleField.getText().trim().isEmpty() || ciudadField.getText().trim().isEmpty()) {
            showMessage("Error de validación", "La calle y ciudad son obligatorias", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    private void showMessage(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}