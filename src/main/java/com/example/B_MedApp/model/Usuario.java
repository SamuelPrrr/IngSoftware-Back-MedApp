package com.example.B_MedApp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Usuario")
@Getter @Setter
@NoArgsConstructor
public abstract class Usuario implements UserDetails { //Implementamos UserDetails para trabajar con la autenticación

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

    @Getter
    @Column(name = "activo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activo = true; // Por defecto, el usuario está activo


    // Método para desactivar
    public void desactivar() {
        this.activo = false;
    }

    @PrePersist
    public void setDefaultRole() {
        if (this.rol == null) {
            this.rol = UserType.PACIENTE; // Definir un rol predeterminado si no se asigna
        }
    }

    // Constructor con parámetros
    public Usuario(String nombre, String correo, String telefono, String sexo, String password, UserType rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.sexo = sexo;
        this.password = password;
        this.rol = rol;
    }


    //Metodos implementados desde la interfaz UserDetails y que nos permite hacer el AUTH
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asegúrate de retornar el rol del usuario como autoridad
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return this.correo; // Usar el correo como nombre de usuario
    }

    //Devolvemos true por que manejamos con el token eso
    @Override
    public boolean isAccountNonExpired() {
        return true; // Para este ejemplo, consideramos que la cuenta no está expirada
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Para este ejemplo, consideramos que la cuenta no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Para este ejemplo, consideramos que las credenciales no están expiradas
    }
}
