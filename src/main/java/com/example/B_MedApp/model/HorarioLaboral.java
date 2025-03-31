package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HorarioLaboral")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    @ManyToOne
    @JoinColumn(name = "idMedico", nullable = false)
    private Medico medico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    @Column(nullable = false)
    private String horaInicio;

    @Column(nullable = false)
    private String horaFin;

    @Column(nullable = false)
    private Boolean disponible = true;
}
