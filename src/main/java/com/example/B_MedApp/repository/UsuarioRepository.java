package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.UserType;
import com.example.B_MedApp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;


//@NoRepositoryBean //With this Spring will not create a definition
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findUsuarioByCorreoAndRol(String correo, UserType rol);
}
