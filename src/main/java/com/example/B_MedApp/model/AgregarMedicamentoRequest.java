package com.example.B_MedApp.model;

import lombok.*;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgregarMedicamentoRequest {
    private Long idMedicamento;
    private LocalTime frecuencia;
    private Integer numeroDias;
    private String cantidadDosis; // Nuevo campo
}