package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.HorarioLaboralRepository;
import com.example.B_MedApp.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final HorarioLaboralRepository horarioLaboralRepository;
    private final PacienteRepository pacienteRepository;
    private final JwtService jwtService;
    private final CitaService citaService;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, HorarioLaboralRepository horarioLaboralRepository, JwtService jwtService, CitaService citaService) {
        this.pacienteRepository = pacienteRepository;
        this.jwtService = new JwtService();
        this.horarioLaboralRepository = horarioLaboralRepository;
        this.citaService = citaService;
    }

    // Obtener todos los pacientes
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAllByRol(UserType.PACIENTE);
    }

    // Obtener paciente por correo
    public ResponseEntity<Object> getAuthenticatedUser(String token) {
        String correo = jwtService.getUsernameFromToken(token);
        Optional<Usuario> paciente = pacienteRepository.findByCorreo(correo);

        HashMap<String, Object> response = new HashMap<>();
        if (paciente.isPresent() && paciente.get().getRol() == UserType.PACIENTE) {
            response.put("error", false);
            response.put("data", paciente.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", true);
            response.put("message", "Paciente no encontrado con correo: " + correo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Crear un nuevo paciente
    public ResponseEntity<Object> savePaciente(Paciente paciente) {
        Optional<Usuario> status = pacienteRepository.findByCorreo(paciente.getCorreo());
        HashMap<String, Object> response = new HashMap<>();
        if (status.isPresent()) {
            response.put("error", true);
            response.put("message", "Ya existe un paciente con ese correo");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        pacienteRepository.save(paciente);
        response.put("message", "Paciente registrado exitosamente");
        response.put("data", paciente);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // HORARIOS
    // Obtener todos los horarios de los médicos en un día específico
    public ResponseEntity<Object> obtenerAllHorariosPorDia(String token, HorarioLaboral horario) {
        // Verificar autenticación del paciente
        ResponseEntity<Object> pacienteResponse = getAuthenticatedUser(token);
        HashMap<String, Object> response = new HashMap<>();

        if (pacienteResponse.getStatusCode() != HttpStatus.OK) {
            response.put("error", true);
            response.put("message", "Paciente no autenticado o no encontrado");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        DiaSemana diaSemana = horario.getDiaSemana();
        // Obtener todos los horarios para el día específico
        List<HorarioLaboral> horarios = horarioLaboralRepository.findHorarioLaboralByDiaSemana(diaSemana);

        if (horarios.isEmpty()) {
            response.put("error", true);
            response.put("message", "No se encontraron horarios para el día: " + diaSemana);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("error", false);
        response.put("data", horarios);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
