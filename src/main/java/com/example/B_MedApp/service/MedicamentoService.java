package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.MedicamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final JwtService jwtService;

    public MedicamentoService(MedicamentoRepository medicamentoRepository, JwtService jwtService) {
        this.medicamentoRepository = medicamentoRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Object> obtenerMedicamentos() {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Medicamento> medicamentos = medicamentoRepository.findAllByOrderByNombreAsc();


            List<Map<String, Object>> medicamentosResponse = medicamentos.stream()
                    .map(med -> {
                        Map<String, Object> medicamentoMap = new LinkedHashMap<>();
                        medicamentoMap.put("id", med.getIdMedicamento());
                        medicamentoMap.put("nombre", med.getNombre());
                        medicamentoMap.put("presentacion", med.getPresentacion());
                        medicamentoMap.put("tipo", med.getTipo());
                        return medicamentoMap;
                    })
                    .collect(Collectors.toList());

            response.put("error", false);
            response.put("data", medicamentosResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener medicamentos: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> obtenerMedicamento(Long idMedicamento) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Medicamento> medicamentoOpt = medicamentoRepository.findById(idMedicamento);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "medicamento no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Medicamento medicamento = medicamentoOpt.get();

            response.put("error", false);
            response.put("data", medicamento);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener medicamento: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 1. Registrar nueva medicamento
    public ResponseEntity<Object> registrarMedicamento(/*String token,*/ Medicamento medicamento) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            /* Aquí ira lo del moderador
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
             */

            // Validar que el medicamento no exista
            Optional<Medicamento> medicamentoOpt = medicamentoRepository.findByNombre(medicamento.getNombre());
            if (medicamentoOpt.isPresent()) {
                response.put("error", true);
                response.put("message", "Medicamento ya existe");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            //Guardar el medicamento
            Medicamento nuevoMedicamento = medicamentoRepository.save(medicamento);

            response.put("error", false);
            response.put("message", "Medicamento registrado exitosamente");
            response.put("data", nuevoMedicamento);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al registrar Medicamento: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> eliminarMedicamento(/*String token, */ Long idMedicamento) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            /*
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

             */

            // Buscar la cita
            Optional<Medicamento> medicamentoOpt = medicamentoRepository.findById(idMedicamento);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            medicamentoRepository.deleteById(idMedicamento);

            response.put("error", false);
            response.put("message", "Medicamento eliminado exitosamente");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar medicamento: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
