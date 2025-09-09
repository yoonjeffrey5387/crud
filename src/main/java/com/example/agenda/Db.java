package com.example.agenda;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Db {
    private static final Properties props = new Properties();

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

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC: " + driver, e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}
