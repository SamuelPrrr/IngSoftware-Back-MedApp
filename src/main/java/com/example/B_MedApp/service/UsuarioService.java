package com.example.B_MedApp.service;

import com.example.B_MedApp.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class UsuarioService {
    @GetMapping
    public List<Usuario> getNames(){
        return List.of(
                new Usuario(
                        5634L,
                        "Samuel",
                        "asamuelpalomares",
                        "password",
                        "PACIENTE"
                )
        );
    }
}
