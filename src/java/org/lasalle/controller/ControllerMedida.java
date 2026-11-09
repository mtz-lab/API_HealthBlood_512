/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import org.lasalle.model.Medida;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author melan
 */
public class ControllerMedida {

    public List<Medida> getAllMedidas() throws SQLException {
        List<Medida> lista = new ArrayList<>();

        String sql = "SELECT idMedida, idUsuario, glucosa, "
                + "DATE_FORMAT(fechaHora, '%Y-%m-%d %H:%i:%s') AS fechaHora, "
                + "contexto, notas FROM medida ORDER BY fechaHora DESC";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No fue posible conectar con la base de datos");
        }

        try (conn;
                PreparedStatement pstm = conn.prepareStatement(sql);
                ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                lista.add(fillMedida(rs));
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    public List<Medida> getMedidasByUsuarioId(int idUsuario) throws SQLException {
        List<Medida> lista = new ArrayList<>();

        String sql = "SELECT idMedida, idUsuario, glucosa, "
                + "DATE_FORMAT(fechaHora, '%Y-%m-%d %H:%i:%s') AS fechaHora, "
                + "contexto, notas FROM medida WHERE idUsuario = ? "
                + "ORDER BY fechaHora DESC";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No fue posible conectar con la base de datos");
        }

        try (conn;
                PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idUsuario);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    lista.add(fillMedida(rs));
                }
            }
        } finally {
            connMysql.close();
        }
        return lista;
    }

    public Medida insertMedida(Medida m) throws SQLException {
        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) "
                + "VALUES (?, ?, COALESCE(?, NOW()), ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();
        if (conn == null) {
            throw new SQLException("No fue posible conectar con la base de datos");
        }

        try (conn;
                PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, m.getIdUsuario());
            pstm.setInt(2, m.getGlucosa());
            pstm.setString(3, m.getFechaHora());
            pstm.setString(4, m.getContexto());
            pstm.setString(5, m.getNotas());
            pstm.executeUpdate();

            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setIdMedida(rs.getInt(1));
                }
            }
            return m;
        } finally {
            connMysql.close();
        }
    }

    private Medida fillMedida(ResultSet rs) throws SQLException {
        Medida medida = new Medida(
                rs.getInt("idMedida"),
                rs.getInt("idUsuario"),
                rs.getInt("glucosa"),
                rs.getString("fechaHora"),
                rs.getString("contexto"),
                rs.getString("notas"));
        return medida;
    }
}
