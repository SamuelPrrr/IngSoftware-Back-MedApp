package com.example.B_MedApp.controller;


import com.example.B_MedApp.model.Usuario;
import com.example.B_MedApp.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> getAll(){
        return this.usuarioService.getUsers();
    }

    @PostMapping(path = "email")
    public ResponseEntity<Object> getByEmail(@RequestBody Map<String, String> request){
        return this.usuarioService.getUserByEmail(request.get("correo"));
    }

    @PostMapping(path = "register")
    public ResponseEntity<Object> save(@RequestBody  Usuario usuario){
        return this.usuarioService.newUser(usuario);
    }
}
