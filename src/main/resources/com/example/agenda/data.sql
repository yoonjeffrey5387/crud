-- Datos de prueba para Personas
INSERT INTO Personas (nombre, email) VALUES ('John Doe', 'john.doe@email.com');
INSERT INTO Personas (nombre, email) VALUES ('Jane Smith', 'jane.smith@email.com');
INSERT INTO Personas (nombre, email) VALUES ('Bob Johnson', 'bob.johnson@email.com');
INSERT INTO Personas (nombre, email) VALUES ('Alice Brown', 'alice.brown@email.com');

-- Datos de prueba para Teléfonos
INSERT INTO Telefonos (personaId, telefono) VALUES (1, '555-1234');
INSERT INTO Telefonos (personaId, telefono) VALUES (1, '555-5678');
INSERT INTO Telefonos (personaId, telefono) VALUES (2, '555-9012');
INSERT INTO Telefonos (personaId, telefono) VALUES (3, '555-3456');
INSERT INTO Telefonos (personaId, telefono) VALUES (3, '555-7890');
INSERT INTO Telefonos (personaId, telefono) VALUES (4, '555-2468');

-- Datos de prueba para Direcciones
INSERT INTO Direcciones (calle, ciudad, estado, codigoPostal, pais) VALUES 
('Calle Principal 123', 'Ciudad A', 'Estado X', '12345', 'País 1');
INSERT INTO Direcciones (calle, ciudad, estado, codigoPostal, pais) VALUES 
('Avenida Central 456', 'Ciudad B', 'Estado Y', '67890', 'País 1');
INSERT INTO Direcciones (calle, ciudad, estado, codigoPostal, pais) VALUES 
('Calle Secundaria 789', 'Ciudad A', 'Estado X', '11111', 'País 1');
INSERT INTO Direcciones (calle, ciudad, estado, codigoPostal, pais) VALUES 
('Boulevard Norte 321', 'Ciudad C', 'Estado Z', '22222', 'País 2');

-- Datos de prueba para PersonaDirecciones (relación muchos a muchos)
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (1, 1);
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (1, 2);
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (2, 1); -- Dirección compartida
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (2, 3);
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (3, 2); -- Dirección compartida
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (3, 4);
INSERT INTO PersonaDirecciones (personaId, direccionId) VALUES (4, 1); -- Dirección compartida