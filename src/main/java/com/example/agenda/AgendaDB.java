package com.example.agenda;

import java.sql.*;

public class AgendaDB {
    public static void main(String[] args) {
        try (Connection con = Db.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("Conectado a: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());

            try (Statement st = con.createStatement()) {
                st.execute("SELECT 1");
                System.out.println("Ping OK");
            }

            try (Statement st = con.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Personas")) {
                    if (rs.next()) {
                        System.out.println("Personas en DB: " + rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.out.println("Aún no existe la tabla Personas. Ejecuta schema.sql en tu DB.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("No se pudo conectar. Revisa db.properties y que MariaDB esté activo.");
        }
    }
}
