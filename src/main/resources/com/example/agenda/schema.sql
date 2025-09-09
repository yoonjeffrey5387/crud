-- Eliminar tablas existentes en orden correcto (respetando foreign keys)
DROP TABLE IF EXISTS PersonaDirecciones;
DROP TABLE IF EXISTS Telefonos;
DROP TABLE IF EXISTS Direcciones;
DROP TABLE IF EXISTS Personas;

-- Crear tabla Personas
CREATE TABLE Personas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE
);

-- Crear tabla Telefonos
CREATE TABLE Telefonos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  personaId INT NOT NULL,
  telefono VARCHAR(20) NOT NULL,
  CONSTRAINT fk_telefono_persona
    FOREIGN KEY (personaId) REFERENCES Personas(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Crear tabla Direcciones
CREATE TABLE Direcciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  calle VARCHAR(200) NOT NULL,
  ciudad VARCHAR(100) NOT NULL,
  estado VARCHAR(100),
  codigoPostal VARCHAR(20),
  pais VARCHAR(100)
);

-- Crear tabla de relación muchos a muchos PersonaDirecciones
CREATE TABLE PersonaDirecciones (
  personaId INT NOT NULL,
  direccionId INT NOT NULL,
  PRIMARY KEY (personaId, direccionId),
  CONSTRAINT fk_persona_direccion_persona
    FOREIGN KEY (personaId) REFERENCES Personas(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_persona_direccion_direccion
    FOREIGN KEY (direccionId) REFERENCES Direcciones(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_personas_nombre ON Personas(nombre);
CREATE INDEX idx_personas_email ON Personas(email);
CREATE INDEX idx_telefonos_persona ON Telefonos(personaId);
CREATE INDEX idx_direcciones_ciudad ON Direcciones(ciudad);
CREATE INDEX idx_persona_direcciones_persona ON PersonaDirecciones(personaId);
CREATE INDEX idx_persona_direcciones_direccion ON PersonaDirecciones(direccionId);