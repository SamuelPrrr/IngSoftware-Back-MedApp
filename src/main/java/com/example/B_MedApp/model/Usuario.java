// Usuario.java (Clase base)
package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Usuario")
@Getter @Setter
@NoArgsConstructor // Lombok genera un constructor vacío
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDUsuario", nullable = false)
    private Long idUsuario;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @Column(name = "Correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "Telefono", unique = true, nullable = false)
    private String telefono;

    @Column(name = "Sexo", nullable = false)
    private String sexo;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Rol")
    @Enumerated(EnumType.STRING)
    private UserType rol;

    @PrePersist
    public void setDefaultRole() {
        if (this.rol == null) {
            this.rol = UserType.PACIENTE;
        }
    }

    // Constructor con parámetros, sin necesidad del ID
    public Usuario(String nombre, String correo, String telefono, String sexo, String password, UserType rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.sexo = sexo;
        this.password = password;
        this.rol = rol;
    }
}
