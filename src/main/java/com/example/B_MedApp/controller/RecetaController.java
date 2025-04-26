package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.AgregarMedicamentoRequest;
import com.example.B_MedApp.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    @Autowired
    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    // Crear nueva receta
    @PostMapping
    public ResponseEntity<Object> crearReceta(@RequestParam Long idCita, @RequestParam String anotaciones) {
        return recetaService.crearReceta(idCita, anotaciones);
    }

    // Obtener receta por ID
    @GetMapping("/{idReceta}")
    public ResponseEntity<Object> getReceta(@PathVariable Long idReceta) {
        return recetaService.obtenerReceta(idReceta);
    }

    @PostMapping("/{idReceta}/medicamentos")
    public ResponseEntity<Object> agregarMedicamento(@PathVariable Long idReceta, @RequestBody AgregarMedicamentoRequest request) {
        return recetaService.agregarMedicamentoAReceta(
                    idReceta,
                    request.getIdMedicamento(),
                    request.getFrecuencia(),
                    request.getNumeroDias(),
                    request.getCantidadDosis()
            );
    }

    // Eliminar medicamento de receta
    @DeleteMapping("/{idReceta}/medicamentos/{idMedicamentoRecetado}")
    public ResponseEntity<Object> eliminarMedicamento(
            @PathVariable Long idReceta,
            @PathVariable Long idMedicamentoRecetado) {
        return recetaService.eliminarMedicamentoDeReceta(idReceta, idMedicamentoRecetado);
    }

    // Eliminar receta completa
    @DeleteMapping("/{idReceta}")
    public ResponseEntity<Object> eliminarReceta(@PathVariable Long idReceta) {
        return recetaService.eliminarReceta(idReceta);
    }

    // Obtener recetas por cita
    @GetMapping("/por-cita/{idCita}")
    public ResponseEntity<Object> obtenerRecetasPorCita(@PathVariable Long idCita) {
        return recetaService.obtenerRecetasPorCita(idCita);
    }
}