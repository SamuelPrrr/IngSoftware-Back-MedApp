package com.example.B_MedApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "MedicamentoRecetado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicamentoRecetado{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedicamentoEnReceta;

    @ManyToOne (cascade = CascadeType.PERSIST)
    private Medicamento medicamento;

    @Column(nullable = false)
    private LocalTime frecuencia; // Ej: cada 7 horas

    @Column(nullable = false)
    private Integer numeroDias;

    @Column(nullable = false)
    private String cantidadDosis;

    @Column(nullable = false)
    private Integer dosisActual = 0; // Cuántas veces lo ha tomado

    @Column(nullable = false)
    private Integer numDosis; // Calculado: (dias * 24 / frecuenciaHoras)

    @JsonIgnore // Evita que se serialice la Receta dentro de MedicamentoRecetado
    @ManyToOne
    private Receta receta;
}
