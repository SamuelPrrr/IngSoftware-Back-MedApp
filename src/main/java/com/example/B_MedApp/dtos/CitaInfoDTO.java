package com.example.B_MedApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitaInfoDTO {
    private Long idCita;
    private LocalDateTime fechaHora;
    private String estado;
}