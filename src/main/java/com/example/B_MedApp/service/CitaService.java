package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.CitaRepository;
import com.example.B_MedApp.repository.MedicoRepository;
import com.example.B_MedApp.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtService jwtService;

    public CitaService(CitaRepository citaRepository, MedicoRepository medicoRepository, PacienteRepository pacienteRepository, JwtService jwtService) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtService = jwtService;
    }

    //idUsuario es el del medico
    public ResponseEntity<Object> registrarCitaPaciente(String token, Long idUsuario, CitaRequest citaRequest) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // 1. Validar token y obtener paciente
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. Validar médico
            Optional<Medico> medicoOpt = medicoRepository.findMedicoByIdUsuario(idUsuario);
            if (medicoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Médico no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 3. Validar fecha/hora
            if (citaRequest.getFechaHora() == null || citaRequest.getFechaHora().isBefore(LocalDateTime.now())) {
                response.put("error", true);
                response.put("message", "Fecha/hora no válida");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 4. Validar disponibilidad
            boolean citaExistente = citaRepository.existsByMedicoAndFechaHoraBetween(
                    medicoOpt.get(),
                    citaRequest.getFechaHora().minusMinutes(29),
                    citaRequest.getFechaHora().plusMinutes(29));

            if (citaExistente) {
                response.put("error", true);
                response.put("message", "El médico ya tiene una cita en ese horario");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            // 5. Crear y guardar la cita
            Cita nuevaCita = new Cita();
            nuevaCita.setPaciente(pacienteOpt.get());
            nuevaCita.setMedico(medicoOpt.get());
            nuevaCita.setFechaHora(citaRequest.getFechaHora());
            nuevaCita.setMotivo(citaRequest.getMotivo());
            nuevaCita.setEstado(EstadoCita.PENDIENTE);
            nuevaCita.setDuracion(citaRequest.getDuracion() != null ? citaRequest.getDuracion() : 30);

            Cita citaGuardada = citaRepository.save(nuevaCita);

            response.put("error", false);
            response.put("message", "Cita registrada exitosamente");
            response.put("data", citaGuardada);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al registrar cita: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
