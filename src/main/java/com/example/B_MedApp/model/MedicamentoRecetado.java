package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "MedicamentoRecetado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    private Receta receta;
}
