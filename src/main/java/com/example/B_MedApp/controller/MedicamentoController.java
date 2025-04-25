package com.example.B_MedApp.controller;

import com.example.B_MedApp.model.EstadoCita;
import com.example.B_MedApp.model.Medicamento;
import com.example.B_MedApp.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {
    private final MedicamentoService medicamentoService;

    @Autowired
    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public ResponseEntity<Object> obtenerMedicamentos() {
        return medicamentoService.obtenerMedicamentos();
    }

    @GetMapping("/{idMedicamento}")
    public ResponseEntity<Object> obtenerMedicamento(@PathVariable Long idMedicamento) {
        return medicamentoService.obtenerMedicamento(idMedicamento);
    }

    @PostMapping()
    public ResponseEntity<Object> guardarMedicamento(@RequestBody Medicamento medicamento) {
        return medicamentoService.registrarMedicamento(medicamento);
    }

    @DeleteMapping("/{idMedicamento}")
    public ResponseEntity<Object> eliminarMedicamento(@PathVariable Long idMedicamento) {
        return medicamentoService.eliminarMedicamento(idMedicamento);
    }

}
