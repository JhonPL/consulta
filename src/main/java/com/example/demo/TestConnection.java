package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/seguimiento_reportes";
        String user = "admin";
        String password = "admin123";

        System.out.println("Intentando conectar a la base de datos...");
        System.out.println("URL: " + url);
        System.out.println("Usuario: " + user);
        System.out.println("Password: " + password);

        try {
            // Cargar el driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✓ Driver PostgreSQL cargado");

            // Intentar conectar
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Conexión exitosa!");

            // Probar una query simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM usuarios");

            if (rs.next()) {
                System.out.println("✓ Usuarios en la BD: " + rs.getInt("total"));
            }

            // Cerrar
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("✓ Test completado exitosamente");

        } catch (Exception e) {
            System.err.println("✗ Error de conexión:");
            e.printStackTrace();
        }
    }
}