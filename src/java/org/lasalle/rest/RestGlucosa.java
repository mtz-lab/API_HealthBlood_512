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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
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
            out = """
                  {"result": "Lista vacia"}
                  """;
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

    
}


