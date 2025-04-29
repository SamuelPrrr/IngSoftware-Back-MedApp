package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.*;
import com.example.B_MedApp.service.CitaService;
import com.example.B_MedApp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CitaService citaService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, CitaService citaService) {
        this.usuarioService = usuarioService;
        this.citaService = citaService;
    }

    // Obtener paciente por correo
    @GetMapping("/profile")
    public ResponseEntity<Object> getAuthenticatedUser(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return usuarioService.getAuthenticatedUser(token);
    }

    // Obtener todos los pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<List<Paciente>> getAllPatients() {
        List<Paciente> pacientes = usuarioService.getPatients();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @GetMapping("/citas")
    public ResponseEntity<Object> obtenerAllCitas() {
        return citaService.obtenerAllCitas();
    }

    @PutMapping("citas/{id}/cancelar")
    public ResponseEntity<Object> actualizarEstadoCita(@PathVariable Long id, @RequestParam EstadoCita nuevoEstado) {
        return citaService.actualizarEstadoCita(id, nuevoEstado);
    }

    // Obtener todos los médicos
    @GetMapping("/medicos")
    public ResponseEntity<List<Medico>> getAllDoctors() {
        List<Medico> medicos = usuarioService.getDoctors();
        return new ResponseEntity<>(medicos, HttpStatus.OK);
    }

    // Obtener un usuario por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Object> getUserByCorreo(@PathVariable String correo) {
        return usuarioService.getUserByCorreo(correo);
    }

    // Actualizar un usuario por ID
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.updateUser(id, usuario);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Object> deleteUsuario(@PathVariable Long id) {
        return usuarioService.deleteUsuario(id);
    }

    // Endpoint adicional para obtener un usuario por ID (puede ser médico o paciente)
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        // Implementación opcional si necesitas buscar por ID sin saber el tipo
        return usuarioService.getUserByCorreo(""); // Modificar según necesidad
    }
}