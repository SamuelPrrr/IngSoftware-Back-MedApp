package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class MedicamentoRecetadoService {

    private final MedicamentoRecetadoRepository medicamentoRecetadoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final RecetaRepository recetaRepository;
    private final JwtService jwtService;

    public MedicamentoRecetadoService(MedicamentoRecetadoRepository medicamentoRecetadoRepository, MedicamentoRepository medicamentoRepository, RecetaRepository recetaRepository, JwtService jwtService) {
        this.medicamentoRecetadoRepository = medicamentoRecetadoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.recetaRepository = recetaRepository;
        this.jwtService = jwtService;
    }

    // 1. Crear medicamento recetado
    public ResponseEntity<Object> crearMedicamentoRecetado(Long idReceta, Long idMedicamento, LocalTime frecuencia, Integer numeroDias) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Verificar receta existente
            Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
            if (recetaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Receta no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Verificar medicamento existente
            Optional<Medicamento> medicamentoOpt = medicamentoRepository.findById(idMedicamento);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Calcular número de dosis
            int numDosis = (numeroDias * 24) / frecuencia.getHour();

            // Crear medicamento recetado
            MedicamentoRecetado nuevoMedicamentoRecetado = new MedicamentoRecetado();
            nuevoMedicamentoRecetado.setMedicamento(medicamentoOpt.get());
            nuevoMedicamentoRecetado.setReceta(recetaOpt.get());
            nuevoMedicamentoRecetado.setFrecuencia(frecuencia);
            nuevoMedicamentoRecetado.setNumeroDias(numeroDias);
            nuevoMedicamentoRecetado.setNumDosis(numDosis);

            MedicamentoRecetado saved = medicamentoRecetadoRepository.save(nuevoMedicamentoRecetado);

            response.put("error", false);
            response.put("message", "Medicamento recetado creado exitosamente");
            response.put("data", saved);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al crear medicamento recetado: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 2. Obtener un medicamento recetado específico
    public ResponseEntity<Object> obtenerMedicamentoRecetado(Long idMedicamentoRecetado) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<MedicamentoRecetado> medicamentoOpt = medicamentoRecetadoRepository.findById(idMedicamentoRecetado);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento recetado no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            MedicamentoRecetado medicamento = medicamentoOpt.get();
            Map<String, Object> medicamentoMap = new HashMap<>();
            medicamentoMap.put("id", medicamento.getIdMedicamentoEnReceta());
            medicamentoMap.put("medicamento", Map.of(
                    "id", medicamento.getMedicamento().getIdMedicamento(),
                    "nombre", medicamento.getMedicamento().getNombre(),
                    "cantidad", medicamento.getMedicamento().getPresentacion()
            ));
            medicamentoMap.put("frecuencia", medicamento.getFrecuencia());
            medicamentoMap.put("numeroDias", medicamento.getNumeroDias());
            medicamentoMap.put("dosisActual", medicamento.getDosisActual());
            medicamentoMap.put("numDosis", medicamento.getNumDosis());
            medicamentoMap.put("recetaId", medicamento.getReceta().getIdReceta());

            response.put("error", false);
            response.put("data", medicamentoMap);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener medicamento recetado: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Eliminar medicamento recetado
    public ResponseEntity<Object> eliminarMedicamentoRecetado(Long idMedicamentoRecetado) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<MedicamentoRecetado> medicamentoOpt = medicamentoRecetadoRepository.findById(idMedicamentoRecetado);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento recetado no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            medicamentoRecetadoRepository.deleteById(idMedicamentoRecetado);

            response.put("error", false);
            response.put("message", "Medicamento recetado eliminado exitosamente");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar medicamento recetado: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //4. Actualizar dosis actual (cuando el paciente toma una dosis)
    public ResponseEntity<Object> registrarDosisTomada(Long idMedicamentoRecetado) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<MedicamentoRecetado> medicamentoOpt = medicamentoRecetadoRepository.findById(idMedicamentoRecetado);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento recetado no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            MedicamentoRecetado medicamento = medicamentoOpt.get();
            if (medicamento.getDosisActual() >= medicamento.getNumDosis()) {
                response.put("error", true);
                response.put("message", "Ya se han tomado todas las dosis prescritas");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            medicamento.setDosisActual(medicamento.getDosisActual() + 1);
            medicamentoRecetadoRepository.save(medicamento);

            response.put("error", false);
            response.put("message", "Dosis registrada exitosamente");
            response.put("dosisActual", medicamento.getDosisActual());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al registrar dosis: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}