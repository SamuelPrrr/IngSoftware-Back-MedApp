package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.Cita;
import com.example.B_MedApp.model.CitaRequest;
import com.example.B_MedApp.model.DiaSemana;
import com.example.B_MedApp.model.EstadoCita;
import com.example.B_MedApp.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes/citas")
public class CitaPacienteController {

    private final CitaService citaService;

    @Autowired
    public CitaPacienteController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping("/{idMedico}")
    //@PreAuthorize("hasAuthority('ROLE_PACIENTE')")
    public ResponseEntity<Object> registrarCita(@RequestHeader("Authorization") String token, @PathVariable Long idMedico, @RequestBody CitaRequest citaRequest) {
        return citaService.registrarCitaPaciente(token.replace("Bearer ", ""), idMedico, citaRequest);
    }

    @GetMapping
    public ResponseEntity<Object> obtenerCitasPaciente(
            @RequestHeader("Authorization") String token) {
        return citaService.obtenerCitasPorPaciente(token.replace("Bearer ", ""));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<Object> obtenerCitasPorFecha(
            @RequestHeader("Authorization") String token,
            @PathVariable String fecha) {
        return citaService.obtenerCitasPorFecha(token.replace("Bearer ", ""), fecha);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Object> actualizarEstadoCita(
            @PathVariable Long id,
            @RequestParam EstadoCita nuevoEstado) {
        return citaService.actualizarEstadoCita(id, nuevoEstado);
    }

    @DeleteMapping("/{idCita}")
    public ResponseEntity<Object> eliminarCita(
            @RequestHeader("Authorization") String token,
            @PathVariable Long idCita) {
        return citaService.eliminarCita(token.replace("Bearer ", ""), idCita);
    }

    //Horarios
    @GetMapping("/disponibilidad/horarios-medicos")
    public ResponseEntity<Object> obtenerHorariosMedicosPorDia(@RequestHeader("Authorization") String token, @RequestParam DiaSemana diaSemana) {

        return citaService.obtenerHorariosMedicosPorDia(
                token.replace("Bearer ", ""),
                diaSemana
        );
    }
    // Endpoint para el frontend
    @PostMapping("/horarios/disponibles")
    public ResponseEntity<Object> obtenerHorariosDisponibles(
            @RequestParam Long medicoId,
            @RequestParam String diaSemana) {
        return citaService.obtenerHorariosDisponibles(medicoId, diaSemana);
    }
}