package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.HorarioLaboralRepository;
import com.example.B_MedApp.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final HorarioLaboralRepository horarioLaboralRepository;
    private final MedicoRepository medicoRepository;
    private final JwtService jwtService;

    @Autowired
    public MedicoService(HorarioLaboralRepository horarioLaboralRepository, MedicoRepository medicoRepository, JwtService jwtService) {
        this.horarioLaboralRepository = horarioLaboralRepository;
        this.medicoRepository = medicoRepository;
        this.jwtService = jwtService;
    }

    // 📌 Obtener médico autenticado desde el token
    public ResponseEntity<Object> getAuthenticatedUser(String token) {
        String correo = jwtService.getUsernameFromToken(token);
        Optional<Usuario> usuario = medicoRepository.findByCorreo(correo);

        HashMap<String, Object> response = new HashMap<>();
        if (usuario.isPresent() && usuario.get().getRol() == UserType.MEDICO) {
            response.put("error", false);
            response.put("data", usuario.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", true);
            response.put("message", "Médico no encontrado con correo: " + correo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // 📌 Método auxiliar para obtener solo el objeto Medico
    private Optional<Medico> getAuthenticatedMedico(String token) {
        ResponseEntity<Object> responseEntity = getAuthenticatedUser(token);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            HashMap<String, Object> body = (HashMap<String, Object>) responseEntity.getBody();
            return Optional.of((Medico) body.get("data"));
        }
        return Optional.empty();
    }

    // 📌 Obtener horarios del médico autenticado (FUNCIONA)
    public ResponseEntity<Object> obtenerHorariosPorMedico(String token) {
        Optional<Medico> medicoOpt = getAuthenticatedMedico(token);
        HashMap<String, Object> response = new HashMap<>();

        if (medicoOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "Médico no autenticado o no encontrado");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<HorarioLaboral> horarios = horarioLaboralRepository.findHorarioByMedico_IdUsuario(medicoOpt.get().getIdUsuario());

        response.put("error", false);
        response.put("data", horarios);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 📌 Registrar horario laboral
    public ResponseEntity<Object> registrarHorario(String token, HorarioLaboral horario) {
        Optional<Medico> medicoOpt = getAuthenticatedMedico(token);
        HashMap<String, Object> response = new HashMap<>();

        if (medicoOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "Médico no autenticado o no encontrado");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        horario.setMedico(medicoOpt.get());
        horarioLaboralRepository.save(horario);
        response.put("message", "Horario laboral registrado exitosamente");
        response.put("data", horario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 📌 Modificar horario laboral
    public ResponseEntity<Object> modificarHorario(String token, HorarioLaboral horarioActualizado) {
        Optional<Medico> medicoOpt = getAuthenticatedMedico(token);
        HashMap<String, Object> response = new HashMap<>();

        if (medicoOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "Médico no autenticado o no encontrado");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Medico medico = medicoOpt.get();

        DiaSemana diaSemana = horarioActualizado.getDiaSemana();

        // Buscar el horario del médico en el día indicado
        Optional<HorarioLaboral> horarioOpt = horarioLaboralRepository.findByMedico_IdUsuarioAndDiaSemana(medico.getIdUsuario(), diaSemana);

        if (horarioOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "No se encontró un horario para el día: " + diaSemana);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Actualizar los valores del horario encontrado
        HorarioLaboral horario = horarioOpt.get();
        horario.setHoraInicio(horarioActualizado.getHoraInicio());
        horario.setHoraFin(horarioActualizado.getHoraFin());
        horario.setDisponible(horarioActualizado.getDisponible());

        horarioLaboralRepository.save(horario);
        response.put("message", "Horario actualizado correctamente");
        response.put("data", horario);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 📌 Eliminar horario laboral
    public ResponseEntity<Object> eliminarHorario(String token, HorarioLaboral horario) {
        Optional<Medico> medicoOpt = getAuthenticatedMedico(token);
        HashMap<String, Object> response = new HashMap<>();

        if (medicoOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "Médico no autenticado o no encontrado");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Medico medico = medicoOpt.get();

         DiaSemana diaSemana = horario.getDiaSemana();
        // Buscar el horario del médico en el día indicado
        Optional<HorarioLaboral> horarioOpt = horarioLaboralRepository.findByMedico_IdUsuarioAndDiaSemana(medico.getIdUsuario(), diaSemana);

        if (horarioOpt.isEmpty()) {
            response.put("error", true);
            response.put("message", "No se encontró un horario para el día: " + diaSemana);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        // Eliminar el horario encontrado
        horarioLaboralRepository.delete(horarioOpt.get());
        response.put("message", "Horario eliminado correctamente para el día: " + diaSemana);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
