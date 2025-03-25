package com.example.B_MedApp.service;

import com.example.B_MedApp.model.Usuario;
import com.example.B_MedApp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getUsers(){
        return this.usuarioRepository.findAll();
    }

    public ResponseEntity<Object> getUserByEmail(String correo) {
        Optional<Usuario> usuario = this.usuarioRepository.findByCorreo(correo);
        HashMap<String, Object> response = new HashMap<>();
        if (usuario.isPresent()) {
            response.put("error", false);
            response.put("data", usuario.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", true);
            response.put("message", "Usuario no encontrado con correo: " + correo);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<Object> newUser(Usuario usuario){
        Optional<Usuario> res = this.usuarioRepository.findByCorreo(usuario.getCorreo());
        HashMap<String, Object> response = new HashMap<>();
        if(res.isPresent()){
            response.put("error", true);
            response.put("message", "Ya existe un usuario con ese correo");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT
            );
        }
        else{
            this.usuarioRepository.save(usuario);
            //Estudiar
            response.put("data", response.getOrDefault("data", new HashMap<>()));
            response.put("message", "Se guardo con exito");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.CREATED
            );
        }
    }
}
