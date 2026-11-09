/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;
import org.lasalle.model.Usuario;
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
public class ControllerUsuario {
    
    public List<Usuario> getUsuarios() throws SQLException{
    List<Usuario> lista = new ArrayList<>();
    
    //declaramos el query a ejecutar
    String sql = "SELECT * FROM usuario";
    ConnectionMysql connMysql = new ConnectionMysql();
    Connection conn = connMysql.open();
    PreparedStatement pstm = conn.prepareStatement(sql);
    ResultSet rs = pstm.executeQuery();
    
    while(rs.next()){
        lista.add(fillUsuario(rs));
    }
    rs.close();
    connMysql.close();
    conn.close();
    return lista;
    
    }
    
    public Usuario getUsuarioById(int idUsuario) throws SQLException{
    String sql = "SELECT * FROM usuario WHERE idUsuario = ?";
    ConnectionMysql connMysql = new ConnectionMysql();
    Connection conn = connMysql.open();
    if (conn == null) {
        throw new SQLException("No fue posible conectar con la base de datos");
    }

    try (conn;
            PreparedStatement pstm = conn.prepareStatement(sql)) {
        pstm.setInt(1, idUsuario);
        try (ResultSet rs = pstm.executeQuery()) {
            if (rs.next()) {
                return fillUsuario(rs);
            }
        }
    } finally {
        connMysql.close();
    }
    return null;
    }

    public Usuario insertUsuario(Usuario usuario) throws SQLException{
    String sql = "INSERT INTO usuario (nombre, primerApellido, segundoApellido, email) "
               + "VALUES (?, ?, ?, ?)";
    ConnectionMysql connMysql = new ConnectionMysql();
    Connection conn = connMysql.open();
    if (conn == null) {
        throw new SQLException("No fue posible conectar con la base de datos");
    }
    PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    try {
        pstm.setString(1, usuario.getNombre());
        pstm.setString(2, usuario.getPrimerApellido());
        pstm.setString(3, usuario.getSegundoApellido());
        pstm.setString(4, usuario.getEmail());
        pstm.executeUpdate();

        ResultSet rs = pstm.getGeneratedKeys();
        if (rs.next()) {
            usuario.setIdUsuario(rs.getInt(1));
        }
        rs.close();

        return usuario;
    } finally {
        pstm.close();
        connMysql.close();
        conn.close();
    }
    }
    
    public Usuario fillUsuario(ResultSet rs) throws SQLException{
    Usuario user = new Usuario();
    user.setIdUsuario(rs.getInt("idUsuario"));
    user.setNombre(rs.getString("nombre"));
    user.setPrimerApellido(rs.getString("primerApellido"));
    user.setSegundoApellido(rs.getString("segundoApellido"));
    user.setEmail(rs.getString("email"));
    return user;
    }
    
}
