package com.example.B_MedApp.dtos;

import lombok.Data;

@Data
public class MedicamentoDTO {
    private Long idMedicamento;
    private String nombre;
    private String presentacion;
    private String tipo;
}