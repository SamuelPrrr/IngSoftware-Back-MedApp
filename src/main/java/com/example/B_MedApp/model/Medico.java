package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Medico")
@Getter @Setter
public class Medico extends Usuario{

    @Column(name = "Especialidad", nullable = false)
    private String especialidad = "General";
}
