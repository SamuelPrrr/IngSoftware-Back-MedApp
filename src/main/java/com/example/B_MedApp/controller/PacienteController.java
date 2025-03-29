package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Obtener todos los pacientes
    @GetMapping
    public List<Paciente> getAllPacientes() {
        return pacienteService.getAllPacientes();
    }

    // Obtener paciente por correo
    @GetMapping("/profile")
    public ResponseEntity<Object> getAuthenticatedUser(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return pacienteService.getAuthenticatedUser(token);
    }

    // Crear un nuevo paciente
    @PostMapping
    public ResponseEntity<Object> createPaciente(@RequestBody Paciente paciente) {
        return pacienteService.savePaciente(paciente);
    }

}
