package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.UserType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends UsuarioRepository {
    List<Medico> findAllByRol(UserType rol);
    Optional<Medico> findMedicoByIdUsuario(Long idUsuario);
}
