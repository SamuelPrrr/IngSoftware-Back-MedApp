package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.DiaSemana;
import com.example.B_MedApp.model.HorarioLaboral;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.service.CitaService;
import com.example.B_MedApp.service.MedicoService;
import com.example.B_MedApp.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService medicoService;
    private final PacienteService pacienteService;

    @Autowired
    public MedicoController(MedicoService medicoService, CitaService citaService, PacienteService pacienteService) {
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
    }

    // Obtener Medico por correo para el perfil
    @GetMapping("/profile")
    public ResponseEntity<Object> getAuthenticatedUser(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return medicoService.getAuthenticatedUser(token);
    }

    // Horarios (CRUD)
    @GetMapping("/get/horarios")
    public ResponseEntity<Object> getHorarios(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return this.medicoService.obtenerHorariosPorMedico(token);
    }

    @PostMapping("/post/horarios")
    public ResponseEntity<Object> postHorarios(@RequestHeader("Authorization") String token, @RequestBody HorarioLaboral horario) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return this.medicoService.registrarHorario(token, horario);
    }

    @PutMapping("/put/horarios")
    public ResponseEntity<Object> putHorarios(@RequestHeader("Authorization") String token, @RequestBody HorarioLaboral horario) {
        token = token.replace("Bearer ", "");
        return this.medicoService.modificarHorario(token, horario);
    }

    @DeleteMapping("/delete/horarios")
    public ResponseEntity<Object> deleteHorario(@RequestHeader("Authorization") String token, @RequestBody HorarioLaboral horario) {
        token = token.replace("Bearer ", "");
        return this.medicoService.eliminarHorario(token, horario);
    }

    @GetMapping("/pacientes")
    public List<Paciente> getPacientes() {
        return this.pacienteService.getAllPacientes();
    }

}
