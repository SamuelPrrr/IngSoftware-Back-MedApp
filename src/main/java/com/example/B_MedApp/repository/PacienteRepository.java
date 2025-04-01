package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.UserType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends UsuarioRepository{
    List<Paciente> findAllByRol(UserType userType);

    Optional<Paciente> findByCorreoAndRol(String correo, UserType rol);
}
