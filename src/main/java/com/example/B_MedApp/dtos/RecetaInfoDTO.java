package com.example.B_MedApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecetaInfoDTO {
    private Long idReceta;
    private String anotaciones;
    private CitaInfoDTO cita; // DTO simplificado de Cita
}