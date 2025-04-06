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

    @PutMapping("/{id}/estado")
    public ResponseEntity<Object> actualizarEstadoCita(@PathVariable Long id, @RequestParam EstadoCita nuevoEstado) {
        return citaService.actualizarEstadoCita(id, nuevoEstado);
    }
}
