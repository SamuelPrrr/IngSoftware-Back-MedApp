package com.example.B_MedApp.dtos;

import com.example.B_MedApp.model.Medicamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicamentoRecetadoDTO {
    private Long idMedicamentoEnReceta;
    private MedicamentoDTO medicamento;
    private LocalTime frecuencia;
    private Integer numeroDias;
    private String cantidadDosis;
    private Integer dosisActual;
    private Integer numDosis;
    private RecetaInfoDTO receta; // DTO simplificado de RecetaInfo
}
