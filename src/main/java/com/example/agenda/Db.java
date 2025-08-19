package com.example.agenda;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class Db {
    private static final Properties props = new Properties();
    private static boolean testMode = false;
    private static boolean initialized = false;

    static {
        try (InputStream in = Db.class.getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException("No se encontró db.properties");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando db.properties", e);
        }
    }

    public static void setTestMode(boolean enabled) {
        testMode = enabled;
    }

    public static Connection getConnection() throws SQLException {
        String prefix = testMode ? "test." : "";
        String url = props.getProperty(prefix + "db.url");
        String user = props.getProperty(prefix + "db.user");
        String password = props.getProperty(prefix + "db.password");
        String driver = props.getProperty(prefix + "db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC: " + driver, e);
        }
        
        Connection connection = DriverManager.getConnection(url, user, password);
        
        // Inicializar tablas si es H2 y no se ha hecho antes
        if (!initialized && driver.contains("h2")) {
            initializeTables(connection);
            initialized = true;
        }
        
        return connection;
    }
    
    private static void initializeTables(Connection connection) throws SQLException {
        try (InputStream schemaStream = Db.class.getResourceAsStream("schema.sql");
             Statement stmt = connection.createStatement()) {
            
            if (schemaStream != null) {
                Scanner scanner = new Scanner(schemaStream).useDelimiter("\\A");
                String schema = scanner.hasNext() ? scanner.next() : "";
                
                // Ejecutar cada statement del schema
                String[] statements = schema.split(";");
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        stmt.executeUpdate(sql);
                    }
                }
                
                System.out.println("Tablas inicializadas correctamente en H2");
            }
        } catch (Exception e) {
            System.err.println("Error inicializando tablas: " + e.getMessage());
            // No lanzamos excepción para que la app pueda continuar
        }
    }
}
