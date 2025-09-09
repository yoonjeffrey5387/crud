# Agenda CRUD JavaFX (Personas + Teléfonos)

Este es un proyecto de ejemplo para gestionar personas y sus teléfonos usando JavaFX y MariaDB (o MySQL).

## Características
- **CRUD Completo**: Crea, lee, actualiza y elimina personas.
- **Teléfonos Múltiples**: Cada persona puede tener varios números de teléfono.
- **Base de Datos**: Utiliza MariaDB/MySQL para almacenar la información.
- **Interfaz Gráfica**: Desarrollado con JavaFX para una experiencia de usuario intuitiva.

## Requisitos
- **Java Development Kit (JDK)**: Versión 17 o superior.
- **Apache Maven**: Versión 3.9 o superior.
- **MariaDB/MySQL**: Un servidor de base de datos funcionando localmente.

## Configuración de la Base de Datos
1.  **Instalar MariaDB/MySQL**: Si no lo tienes, instálalo en tu máquina.
2.  **Crear la base de datos `agenda`**:
    ```sql
    CREATE DATABASE agenda;
    ```
3.  **Crear un usuario y otorgar permisos**:
    ```sql
    CREATE USER 'usuario1'@'localhost' IDENTIFIED BY 'superpassword';
    GRANT ALL PRIVILEGES ON agenda.* TO 'usuario1'@'localhost';
    FLUSH PRIVILEGES;
    ```
4.  **Ejecutar el esquema de tablas**:
    Abre el archivo `src/main/resources/com/example/agenda/schema.sql` y ejecuta su contenido en tu base de datos `agenda`. Esto creará las tablas `Personas` y `Telefonos`.
5.  **Insertar datos de prueba (opcional)**:
    Abre el archivo `src/main/resources/com/example/agenda/data.sql` y ejecuta su contenido para tener algunos datos iniciales.
6.  **Configurar la conexión en Java**:
    Abre `src/main/resources/com/example/agenda/db.properties` y asegúrate de que las credenciales (`db.url`, `db.user`, `db.password`) coincidan con tu configuración de MariaDB/MySQL.

## Cómo Ejecutar la Aplicación
1.  Abre una terminal en la raíz del proyecto.
2.  Ejecuta el siguiente comando para compilar y lanzar la aplicación:
    ```bash
    mvn clean javafx:run
    ```

## Estructura del Proyecto
- `src/main/java/com/example/agenda/`: Contiene el código fuente Java.
    - `App.java`: Punto de entrada de la aplicación JavaFX.
    - `MainController.java`: Lógica de la interfaz de usuario.
    - `model/`: Clases de modelo (ej. `Person`).
    - `dao/`: Clases de acceso a datos (ej. `PersonDao`).
    - `Db.java`: Utilidad para la conexión a la base de datos.
- `src/main/resources/com/example/agenda/`: Contiene recursos.
    - `main-view.fxml`: Definición de la interfaz de usuario.
    - `db.properties`: Configuración de la base de datos.
    - `schema.sql`: Script para crear tablas.
    - `data.sql`: Script para insertar datos de prueba.
- `pom.xml`: Archivo de configuración de Maven.

---
**Desarrollado por Jeffrey Yoon 1196854**
