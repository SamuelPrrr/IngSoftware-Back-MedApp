package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.Cita;
import com.example.B_MedApp.model.CitaRequest;
import com.example.B_MedApp.model.HorarioLaboral;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.repository.CitaRepository;
import com.example.B_MedApp.service.CitaService;
import com.example.B_MedApp.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final CitaService citaService;
    private final CitaRepository citaRepository;

    @Autowired
    public PacienteController(PacienteService pacienteService, CitaService citaService, CitaRepository citaRepository) {
        this.pacienteService = pacienteService;
        this.citaService = citaService;
        this.citaRepository = citaRepository;
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
