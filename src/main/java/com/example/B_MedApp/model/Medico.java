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

}
