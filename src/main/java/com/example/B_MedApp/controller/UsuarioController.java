package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.Usuario;
import com.example.B_MedApp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Obtener todos los pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<List<Paciente>> getAllPatients() {
        List<Paciente> pacientes = usuarioService.getPatients();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    // Obtener todos los médicos
    @GetMapping("/medicos")
    public ResponseEntity<List<Usuario>> getAllDoctors() {
        List<Usuario> medicos = usuarioService.getDoctors();
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
}
