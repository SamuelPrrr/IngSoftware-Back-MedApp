package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.CitaRepository;
import com.example.B_MedApp.repository.HorarioLaboralRepository;
import com.example.B_MedApp.repository.MedicoRepository;
import com.example.B_MedApp.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final HorarioLaboralRepository horarioLaboralRepository;
    private final JwtService jwtService;

    public CitaService(CitaRepository citaRepository, MedicoRepository medicoRepository,
                       PacienteRepository pacienteRepository, JwtService jwtService, HorarioLaboralRepository horarioLaboralRepository) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.jwtService = jwtService;
        this.horarioLaboralRepository = horarioLaboralRepository;
    }

    // 1. Registrar nueva cita
    public ResponseEntity<Object> registrarCitaPaciente(String token, Long idUsuario, CitaRequest citaRequest) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Validar token y obtener paciente
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Validar médico
            Optional<Medico> medicoOpt = medicoRepository.findMedicoByIdUsuario(idUsuario);
            if (medicoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Médico no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Validar fecha/hora
            if (citaRequest.getFechaHora() == null || citaRequest.getFechaHora().isBefore(LocalDateTime.now())) {
                response.put("error", true);
                response.put("message", "Fecha/hora no válida");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Validar disponibilidad
            boolean citaExistente = citaRepository.existsByMedicoAndFechaHoraBetween(
                    medicoOpt.get(),
                    citaRequest.getFechaHora().minusMinutes(29),
                    citaRequest.getFechaHora().plusMinutes(29));

            if (citaExistente) {
                response.put("error", true);
                response.put("message", "El médico ya tiene una cita en ese horario");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            // Crear y guardar la cita
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

    // 2. Obtener todas las citas de un paciente (Se ocupa en Profile/Citas)
    public ResponseEntity<Object> obtenerCitasPorPaciente(String token) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            List<Cita> citas = citaRepository.findByPacienteOrderByFechaHoraDesc(pacienteOpt.get());

            // Puedes personalizar los datos que devuelves
            List<Map<String, Object>> citasResponse = citas.stream().map(cita -> {
                Map<String, Object> citaMap = new HashMap<>();
                citaMap.put("id", cita.getIdCita());
                citaMap.put("fechaHora", cita.getFechaHora());
                citaMap.put("motivo", cita.getMotivo());
                citaMap.put("estado", cita.getEstado());
                citaMap.put("medico", Map.of(
                        "id", cita.getMedico().getIdUsuario(),
                        "nombre", cita.getMedico().getNombre(),
                        "especialidad", cita.getMedico().getEspecialidad()
                ));
                return citaMap;
            }).collect(Collectors.toList());

            response.put("error", false);
            response.put("data", citasResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener citas: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Obtener citas por fecha específica (Se ocupa para Book (agendar citas))
    public ResponseEntity<Object> obtenerCitasPorFecha(String token, String fechaStr) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (Exception e) {
                response.put("error", true);
                response.put("message", "Formato de fecha inválido. Use YYYY-MM-DD");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            LocalDateTime inicioDia = fecha.atStartOfDay();
            LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

            List<Cita> citas = citaRepository.findByPacienteAndFechaHoraBetween(
                    pacienteOpt.get(), inicioDia, finDia);

            response.put("error", false);
            response.put("data", citas);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener citas por fecha: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Eliminar una cita
    public ResponseEntity<Object> eliminarCita(String token, Long citaId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Validar token y paciente
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Buscar la cita
            Optional<Cita> citaOpt = citaRepository.findById(citaId);
            if (citaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Cita no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Cita cita = citaOpt.get();

            // Verificar que la cita pertenece al paciente
            if (!cita.getPaciente().getIdUsuario().equals(pacienteOpt.get().getIdUsuario())) {
                response.put("error", true);
                response.put("message", "No tienes permiso para eliminar esta cita");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Verificar que la cita no esté en el pasado
            if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
                response.put("error", true);
                response.put("message", "No se puede eliminar una cita pasada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            citaRepository.delete(cita);

            response.put("error", false);
            response.put("message", "Cita eliminada exitosamente");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar cita: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Lógica para horarios
    public ResponseEntity<Object> obtenerHorariosMedicosPorDia(String token, DiaSemana diaSemana) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Verificación de autenticación (se mantiene igual)
            String correo = jwtService.getUsernameFromToken(token);
            Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreoAndRol(correo, UserType.PACIENTE);

            if (pacienteOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Paciente no autenticado o no encontrado");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // Obtener horarios
            List<HorarioLaboral> horarios = horarioLaboralRepository.findByDiaSemana(diaSemana);

            if (horarios.isEmpty()) {
                response.put("error", true);
                response.put("message", "No hay horarios disponibles para " + diaSemana);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Formatear respuesta incluyendo el sexo
            List<Map<String, Object>> horariosFormateados = horarios.stream().map(horario -> {
                Map<String, Object> horarioMap = new HashMap<>();
                Medico medico = horario.getMedico();

                horarioMap.put("medicoId", medico.getIdUsuario());
                horarioMap.put("medicoNombre", medico.getNombre());
                horarioMap.put("especialidad", medico.getEspecialidad());
                horarioMap.put("horaInicio", horario.getHoraInicio());
                horarioMap.put("horaFin", horario.getHoraFin());
                horarioMap.put("sexo", medico.getSexo()); // Añade esta línea

                return horarioMap;
            }).collect(Collectors.toList());

            response.put("error", false);
            response.put("data", horariosFormateados);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener horarios: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Todavia no implementado, servira para la busqueda
    // 6. Obtener horarios disponibles por médico y día (para el frontend)
    public ResponseEntity<Object> obtenerHorariosDisponibles(Long medicoId, String diaSemanaStr) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Medico> medicoOpt = medicoRepository.findMedicoByIdUsuario(medicoId);
            if (medicoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Médico no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Medico medico = medicoOpt.get();
            DiaSemana diaSemana;

            try {
                diaSemana = DiaSemana.valueOf(diaSemanaStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                response.put("error", true);
                response.put("message", "Día de la semana no válido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Aquí deberías implementar la lógica para obtener los horarios disponibles
            // basado en el horario del médico y las citas ya agendadas
            // Este es un ejemplo simplificado:

            // 1. Obtener el horario del médico para ese día
            // 2. Obtener las citas ya agendadas para ese médico en ese día
            // 3. Calcular los slots disponibles

            // Ejemplo de respuesta (debes adaptarlo a tu lógica real):
            Map<String, Object> horarioData = new HashMap<>();
            horarioData.put("medico", Map.of(
                    "id", medico.getIdUsuario(),
                    "nombre", medico.getNombre(),
                    "especialidad", medico.getEspecialidad()
            ));
            horarioData.put("horaInicio", "09:00");
            horarioData.put("horaFin", "17:00");

            response.put("error", false);
            response.put("data", horarioData);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener horarios: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> actualizarEstadoCita(Long idCita, EstadoCita nuevoEstado) {
        //String correo = jwtService.getUsernameFromToken(token);
        //Optional<Usuario> paciente = pacienteRepository.findByCorreo(correo);
        HashMap<String, Object> response = new HashMap<>();

        try {

            Optional<Cita> citaOptional = citaRepository.findById(idCita);

            if (citaOptional.isEmpty()) {
                response.put("error", true);
                response.put("message", "Cita no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Cita cita = citaOptional.get();

            // Verificar que la cita pertenece al paciente autenticado
//            if (!cita.getPaciente().equals(paciente.get())) {
//                response.put("error", true);
//                response.put("message", "No tienes permiso para modificar esta cita");
//                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//            }

            // Validar transiciones de estado permitidas
            if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
                response.put("error", true);
                response.put("message", "No se puede modificar una cita ya cancelada o completada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Validar que el nuevo estado sea válido (confirmada o cancelada)
            if (nuevoEstado != EstadoCita.CONFIRMADA && nuevoEstado != EstadoCita.CANCELADA) {
                response.put("error", true);
                response.put("message", "Estado no válido. Solo se permite confirmar o cancelar");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Actualizar estado
            cita.setEstado(nuevoEstado);
            Cita citaActualizada = citaRepository.save(cita);

            // Construir respuesta exitosa
            response.put("error", false);
            response.put("message", "Estado de cita actualizado exitosamente");
            response.put("data", citaActualizada);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al actualizar estado de cita: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private void notificarCancelacion(Cita cita) {
        // Implementar lógica de notificación (email, push notification, etc.)
        // Ejemplo simplificado:
        String mensaje = String.format(
                "La cita del paciente %s para el %s ha sido cancelada",
                cita.getPaciente().getNombre(),
                cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        System.out.println("Notificación: " + mensaje);
        // Aquí iría el código para enviar notificación real al médico
    }
}