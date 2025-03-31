package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.UserType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoRepository extends UsuarioRepository {
    List<Medico> findAllByRol(UserType rol);
    Medico findMedicoByIdUsuario(Long idUsuario);
}
