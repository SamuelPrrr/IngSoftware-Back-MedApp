package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Medico")
@Getter @Setter
public class Medico extends Usuario{

    @Column(name = "Especialidad", nullable = false)
    private String especialidad = "General";

    public Medico() {
        super(); // Llama al constructor sin parámetros de la clase base Usuario
    }
    public Medico(String nombre, String correo, String telefono, String sexo, String password, UserType userType) {
        super(nombre, correo, telefono, sexo, password, userType); // Llama al constructor de Usuario
    }

}


