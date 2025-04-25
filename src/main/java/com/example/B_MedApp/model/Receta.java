package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Receta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idReceta;

    @ManyToOne
    @JoinColumn(name = "idCita", nullable = false)
    private Cita cita;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String anotaciones; // Observaciones

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicamentoRecetado> medicamentos = new ArrayList<>();
}
