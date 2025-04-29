package com.example.B_MedApp.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    //AuthResponse lleva el token
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //Nota: Cambiar el valor que devuelve con un ResponseEntity
    @PostMapping(value = "/register")
    public String register(@RequestBody RegisterRequest request) {
        return String.valueOf(authService.register(request));
    }

    @PostMapping(value = "/registerMed")
    public String registerMed(@RequestBody RegisterRequest request) {
        return String.valueOf(authService.registerMed(request));
    }

    @PostMapping(value = "/registerUsr")
    public String registerUsr(@RequestBody RegisterRequest request) {
        return String.valueOf(authService.registerAdmin(request));
    }

    @GetMapping(value = "/route")
    public ResponseEntity<Object> route(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // Remueve 'Bearer ' del token
        return authService.getAuthenticatedUser(token);
    }
}
