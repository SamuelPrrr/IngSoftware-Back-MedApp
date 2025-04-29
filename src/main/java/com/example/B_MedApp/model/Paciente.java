// Paciente.java (Clase hija)
package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Paciente")
@Getter @Setter
public class Paciente extends Usuario {

    @Column(name = "Altura")
    private Double altura;

    @Column(name = "Peso")
    private Double peso;

    @Column(name = "Edad")
    private Integer edad;

    @Column(name = "Dirección")
    private String direccion;

    public Paciente() {
        super(); // Llama al constructor sin parámetros de la clase base Usuario
    }
    // Constructor con parámetros
    public Paciente(String nombre, String correo, String telefono, String sexo, String password, UserType rol, Double altura, Double peso, Integer edad, String direccion) {
        super(nombre, correo, telefono, sexo, password, rol); // Llama al constructor de Usuario
        this.altura = altura;
        this.peso = peso;
        this.edad = edad;
        this.direccion = direccion;
    }

    public Paciente(String nombre, String correo, String telefono, String sexo, String password, UserType userType) {
        super(nombre, correo, telefono, sexo, password, userType); // Llama al constructor de Usuario
    }
}