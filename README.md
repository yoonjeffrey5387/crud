# Agenda CRUD JavaFX (Personas + Teléfonos)

Proyecto de ejemplo para un CRUD con JavaFX + MariaDB (o MySQL) usando JDBC y JUnit 5.

## Requisitos
- JDK 17+
- Maven 3.9+
- MariaDB/MySQL en localhost (o ajustar `src/main/resources/com/example/agenda/db.properties`)

## Cómo ejecutar
```bash
# 1) Crear DB/tablas (si no existen). Puedes ejecutar el SQL en tu servidor:
#   - src/main/resources/com/example/agenda/schema.sql
#   - src/main/resources/com/example/agenda/data.sql (opcional)

# 2) Ajusta credenciales en:
#   src/main/resources/com/example/agenda/db.properties

# 3) Ejecuta la app JavaFX
mvn -q clean javafx:run

# 4) Ejecuta pruebas
mvn -q test
```

## Notas
- Para pruebas unitarias se usa H2 en modo MySQL.
- Para integración real con MariaDB, asegúrate de que el servicio está activo y los datos de `db.properties` son correctos.
