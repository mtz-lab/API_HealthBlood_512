/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Conexion a MySQL / TiDB.
 *
 * La configuracion se toma de variables de entorno para poder desplegar en la
 * nube. Si las variables no existen, se usan los valores locales de desarrollo.
 *
 * Variables soportadas:
 *   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_SSL
 *
 * @author melan
 */
public class ConnectionMysql {

    Connection conn;

    /**
     * Lee una variable de entorno y, si no existe, una propiedad del sistema.
     * Si tampoco existe, devuelve el valor por defecto.
     */
    private String config(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(name);
        }
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    public Connection open() {
        String host = config("DB_HOST", "127.0.0.1");
        String port = config("DB_PORT", "3306");
        String dbName = config("DB_NAME", "gluco_api");
        String user = config("DB_USER", "root");
        String password = config("DB_PASSWORD", "root");
        boolean ssl = Boolean.parseBoolean(config("DB_SSL", "false"));

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useUnicode=true"
                + "&characterEncoding=utf-8"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true"
                + "&connectTimeout=15000"
                + "&socketTimeout=30000"
                + (ssl ? "&sslMode=REQUIRED" : "&sslMode=DISABLED");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            System.err.println("No fue posible conectar a la base de datos en "
                    + host + ":" + port + "/" + dbName + " -> " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
