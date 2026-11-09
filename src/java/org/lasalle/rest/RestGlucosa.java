/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.lasalle.controller.ConnectionMysql;
import org.lasalle.controller.ControllerUsuario;
import org.lasalle.controller.ControllerMedida;
import org.lasalle.model.Usuario;
import org.lasalle.model.Medida;
import java.util.List;
/**
 *
 * @author melan
 */

@Path("glucosa")
public class RestGlucosa {
    
    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String respuesta = """
                           {"response" : "hola desde java" }
                           """;
                return Response.ok(respuesta).build();
    }
    
    @Path("saludarPersonalizado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarPersonalizado(
    @QueryParam("nombre") String nombre
    
    ){
        String respuesta = """
                           {"response" : "hola %s desde java" }
                           """;
        respuesta = String.format(respuesta, nombre);
        return Response.ok(respuesta).build();
    }
    
    @Path("cuadrado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response numeroCuadrado(
    @QueryParam("numero") int numero
    
    ){
        int cuadrado = numero * numero;
        String respuesta = """
                           {"response" : "el numero cuadrado de %s es %s"}
                        """;
        respuesta = String.format(respuesta, numero, cuadrado);
        return Response.ok(respuesta).build();
    }

    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuario(){
        ControllerUsuario cu = new ControllerUsuario();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cu.getUsuarios();
            out = gson.toJson(lista);
        
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"No fue posible consultar los usuarios. Revisa /api/glucosa/dbDiagnostico\"}")
                    .build();
        }
        return Response.ok(out).build();
    }
    
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        ControllerMedida cm = new ControllerMedida();
        List<Medida> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cm.getAllMedidas();
            out = gson.toJson(lista);
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"Error al consultar las medidas\"}")
                    .build();
        }
        return Response.ok(out).build();
    }

    @Path("getMedidasByUsuarioId")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidasByUsuarioId(
            @QueryParam("idUsuario") int idUsuario){
        ControllerMedida cm = new ControllerMedida();
        List<Medida> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cm.getMedidasByUsuarioId(idUsuario);
            out = gson.toJson(lista);
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"Error al consultar las medidas\"}")
                    .build();
        }
        return Response.ok(out).build();
    }

    @Path("getUsuarioById")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioById(
            @QueryParam("idUsuario") int idUsuario){
        ControllerUsuario cu = new ControllerUsuario();
        Gson gson = new Gson();
        try{
            Usuario usuario = cu.getUsuarioById(idUsuario);
            if (usuario == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"result\":\"Usuario no encontrado\"}")
                        .build();
            }
            return Response.ok(gson.toJson(usuario)).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"Error al consultar el usuario\"}")
                    .build();
        }
    }

    @Path("insertMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertMedida(
            @FormParam("idUsuario") Integer idUsuario,
            @FormParam("glucosa") Integer glucosa,
            @FormParam("fechaHora") String fechaHora,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas) {

        Gson gson = new Gson();

        if (idUsuario == null || glucosa == null || glucosa <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"result\":\"idUsuario y glucosa son obligatorios\"}")
                    .build();
        }

        if (fechaHora != null && fechaHora.trim().isEmpty()) {
            fechaHora = null;
        }

        Medida medida = new Medida();
        medida.setIdUsuario(idUsuario);
        medida.setGlucosa(glucosa);
        medida.setFechaHora(fechaHora);
        medida.setContexto(contexto);
        medida.setNotas(notas);

        try {
            ControllerMedida cm = new ControllerMedida();
            Medida creada = cm.insertMedida(medida);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(creada))
                    .build();
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"result\":\"El usuario no existe\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"No fue posible guardar la medida\"}")
                    .build();
        }
    }

    @Path("insertUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email) {

        Gson gson = new Gson();

        if (nombre == null || nombre.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"result\":\"nombre y email son obligatorios\"}")
                    .build();
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        usuario.setEmail(email.trim());

        try {
            ControllerUsuario cu = new ControllerUsuario();
            Usuario creado = cu.insertUsuario(usuario);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(creado))
                    .build();
        } catch (SQLIntegrityConstraintViolationException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"result\":\"El email ya está registrado\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"result\":\"No fue posible guardar el usuario\"}")
                    .build();
        }
    }

    /**
     * Diagnostico de la conexion. No muestra la contrasena y solo muestra el
     * usuario enmascarado. Sirve para comprobar si las variables de entorno
     * del hosting (Render) llegaron al proceso y si la base de datos responde.
     */
    @Path("dbDiagnostico")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response dbDiagnostico() {
        Gson gson = new Gson();
        Map<String, String> info = new LinkedHashMap<>();

        String host = ConnectionMysql.config("DB_HOST", "127.0.0.1");
        String port = ConnectionMysql.config("DB_PORT", "3306");
        String dbName = ConnectionMysql.config("DB_NAME", "gluco_api");
        String user = ConnectionMysql.config("DB_USER", "root");
        boolean ssl = Boolean.parseBoolean(ConnectionMysql.config("DB_SSL", "false"));

        info.put("DB_HOST_definida", ConnectionMysql.presente("DB_HOST") ? "si" : "NO (se usa el valor local)");
        info.put("host", host);
        info.put("puerto", port);
        info.put("baseDeDatos", dbName);
        info.put("DB_USER_definida", ConnectionMysql.presente("DB_USER") ? "si" : "NO (se usa el valor local)");
        info.put("usuario", enmascarar(user));
        info.put("DB_PASSWORD_definida", ConnectionMysql.presente("DB_PASSWORD") ? "si" : "NO");
        info.put("ssl", String.valueOf(ssl));

        ConnectionMysql conexion = new ConnectionMysql();
        Connection conn = conexion.open();
        if (conn == null) {
            info.put("conexion", "ERROR: no se pudo conectar");
        } else {
            try (Connection c = conn) {
                DatabaseMetaData md = c.getMetaData();
                info.put("conexion", "OK");
                info.put("servidor", md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
                info.put("tabla_usuario", existeTabla(c, "usuario") ? "OK" : "NO EXISTE");
                info.put("tabla_medida", existeTabla(c, "medida") ? "OK" : "NO EXISTE");
                info.put("registros_usuario", contar(c, "usuario"));
                info.put("registros_medida", contar(c, "medida"));
            } catch (Exception e) {
                info.put("conexion", "OK, pero fallo la consulta de metadatos: " + e.getMessage());
            }
        }

        return Response.ok(gson.toJson(info)).build();
    }

    private String enmascarar(String texto) {
        if (texto == null || texto.length() <= 4) {
            return "***";
        }
        return texto.substring(0, Math.min(6, texto.length())) + "***";
    }

    private boolean existeTabla(Connection c, String tabla) {
        try (ResultSet rs = c.getMetaData().getTables(c.getCatalog(), null, tabla,
                new String[]{"TABLE"})) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    private String contar(Connection c, String tabla) {
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            return rs.next() ? String.valueOf(rs.getLong(1)) : "?";
        } catch (Exception e) {
            return "error";
        }
    }
}


