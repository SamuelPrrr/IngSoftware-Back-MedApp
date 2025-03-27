package com.example.B_MedApp.auth;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.UserType;
import com.example.B_MedApp.repository.PacienteRepository;
import com.example.B_MedApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtservice;
    private final PasswordEncoder passwordEncoder; // Esto se encarga en de ecriptar
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));
        UserDetails user = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();
        String token = jwtservice.getToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse register(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // Creación del usuario con builder
        Paciente user = new Paciente( // Si Usuario es una clase abstracta, usa Paciente en lugar de Usuario si no puedes instanciar Usuario directamente
                request.getNombre(),
                request.getCorreo(),
                request.getTelefono(),
                request.getSexo(),
                encodedPassword,
                UserType.PACIENTE
        );

        // Guarda el usuario en el repositorio
        pacienteRepository.save(user);

        //La anotación builder nos permite hacer constructores en el authresponse si los maneje
        // pero con mis modelos no debido a que se confunde un poco con la herencia
        //Para evitar errores maneje sus constructores manualmente

        return AuthResponse.builder()
                .token(jwtservice.getToken(user))
                .build();
    }

}
