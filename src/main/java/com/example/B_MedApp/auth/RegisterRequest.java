package com.example.B_MedApp.auth;

import com.example.B_MedApp.model.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String sexo;
    private String password;
    private UserType rol;

}
