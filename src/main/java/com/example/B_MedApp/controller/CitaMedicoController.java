package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.Cita;
import com.example.B_MedApp.model.EstadoCita;
import com.example.B_MedApp.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/medicos/citas")
public class CitaMedicoController {

    private final CitaService citaService;

    @Autowired
    public CitaMedicoController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<Object> obtenerCitasMedico(@RequestHeader ("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return this.citaService.obtenerCitasPorMedico(token);
    }

    @GetMapping("{idCita}")
    public ResponseEntity<Object> obtenerCita(@RequestHeader ("Authorization") String token, @PathVariable Long idCita) {
        token = token.replace("Bearer ", "");
        return this.citaService.obtenerCita(token, idCita);
    }

    @GetMapping("/pacientes")
    public ResponseEntity<Object> obtenerPacientes(@RequestHeader ("Authorization")  String token) {
        token = token.replace("Bearer ", "");
        return  this.citaService.obtenerPacientesPorMedico(token);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Object> actualizarEstadoCita(@PathVariable Long id, @RequestParam EstadoCita nuevoEstado) {
        return citaService.actualizarEstadoCita(id, nuevoEstado);
    }

}
