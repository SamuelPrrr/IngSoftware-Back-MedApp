package com.example.B_MedApp.service;

import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.UserType;
import com.example.B_MedApp.model.Usuario;
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

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // Obtener todos los pacientes
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAllByRol(UserType.PACIENTE);
    }

    // Obtener paciente por correo
    public ResponseEntity<Object> getPacienteByCorreo(String correo) {
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
}
