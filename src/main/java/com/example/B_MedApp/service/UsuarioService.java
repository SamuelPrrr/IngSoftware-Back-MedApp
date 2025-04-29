package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.UserType;
import com.example.B_MedApp.model.Usuario;
import com.example.B_MedApp.repository.PacienteRepository;
import com.example.B_MedApp.repository.MedicoRepository;
import com.example.B_MedApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Autowired
    public UsuarioService(PacienteRepository pacienteRepository, MedicoRepository medicoRepository, UsuarioRepository usuarioRepository,JwtService jwtService) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Object> getAuthenticatedUser(String token) {
        String correo = jwtService.getUsernameFromToken(token);
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);

        HashMap<String, Object> response = new HashMap<>();
        if (usuario.isPresent() && usuario.get().getRol() == UserType.ADMINISTRADOR) {
            response.put("error", false);
            response.put("data", usuario.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", true);
            response.put("message", "Paciente no encontrado con correo: " + correo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Obtener todos los pacientes
    public List<Paciente> getPatients() {
        return this.pacienteRepository.findAllByRol(UserType.PACIENTE);
    }

    public ResponseEntity<Object> deleteUsuario(Long pacienteId) {
        Optional<Usuario> status = usuarioRepository.findById(pacienteId);
        HashMap<String, Object> response = new HashMap<>();
        try{
            if (status.isPresent()) {
                Usuario usuario = status.get();
                usuarioRepository.delete(usuario);
                response.put("error", false);
                response.put("message", "paciente eliminado correctamente");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.put("error", true);
            response.put("message", "Paciente no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar paciente");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener todos los médicos
    public List<Medico> getDoctors() {
        return this.medicoRepository.findAllByRol(UserType.MEDICO);
    }

    // Buscar usuario por correo
    public ResponseEntity<Object> getUserByCorreo(String correo) {
        Optional<Usuario> usuario = this.pacienteRepository.findByCorreo(correo);
        if (!usuario.isPresent()) {
            usuario = this.medicoRepository.findByCorreo(correo);
        }
        HashMap<String, Object> response = new HashMap<>();
        if (usuario.isPresent()) {
            response.put("error", false);
            response.put("data", usuario.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", true);
            response.put("message", "Usuario no encontrado con correo: " + correo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar usuario
    public ResponseEntity<Object> updateUser(Long id, Usuario usuario) {
        HashMap<String, Object> response = new HashMap<>();
        Optional<Usuario> existingUser = pacienteRepository.findById(id);
        if (!existingUser.isPresent()) {
            existingUser = medicoRepository.findById(id);
        }

        if (!existingUser.isPresent()) {
            response.put("error", true);
            response.put("message", "Usuario no encontrado con ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Aquí actualizas el usuario
        if (usuario instanceof Paciente) {
            Paciente paciente = (Paciente) existingUser.get();
            paciente.setNombre(usuario.getNombre());
            paciente.setCorreo(usuario.getCorreo());
            paciente.setTelefono(usuario.getTelefono());
            paciente.setSexo(usuario.getSexo());
            paciente.setPassword(usuario.getPassword());
            pacienteRepository.save(paciente);
        } else if (usuario instanceof Medico) {
            Medico medico = (Medico) existingUser.get();
            medico.setNombre(usuario.getNombre());
            medico.setCorreo(usuario.getCorreo());
            medico.setTelefono(usuario.getTelefono());
            medico.setSexo(usuario.getSexo());
            medico.setPassword(usuario.getPassword());
            medicoRepository.save(medico);
        }

        response.put("message", "Usuario actualizado con éxito");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
