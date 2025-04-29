package com.example.B_MedApp.auth;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.Paciente;
import com.example.B_MedApp.model.UserType;
import com.example.B_MedApp.model.Usuario;
import com.example.B_MedApp.repository.MedicoRepository;
import com.example.B_MedApp.repository.PacienteRepository;
import com.example.B_MedApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtservice;
    private final PasswordEncoder passwordEncoder; // Esto se encarga en de ecriptar
    private final AuthenticationManager authenticationManager;
    private final MedicoRepository medicoRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));
        UserDetails user = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        String token = jwtservice.getToken(user);
        return new AuthResponse(token);
    }

    //Para que nos rediriga a sus tabs correspondientes
    public ResponseEntity<Object> getAuthenticatedUser(String token) {
        String correo = jwtservice.getUsernameFromToken(token);
        Optional<Usuario> userOptional = usuarioRepository.findByCorreo(correo);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", true,
                            "message", "Usuario no encontrado con correo: " + correo
                    ));
        }

        Usuario user = userOptional.get();
        Map<String, Object> response = new HashMap<>();

        switch (user.getRol()) {
            case PACIENTE:
                response.put("message", "El usuario es Paciente");
                response.put("data", "/(tabs)/profile");
                break;
            case MEDICO:
                response.put("message", "El usuario es Médico");
                response.put("data", "/(medTabs)/profile"); // Ejemplo de ruta
                break;
            case ADMINISTRADOR:
                response.put("message", "El usuario es Administrador");
                response.put("data", "/(admin)/profile"); // Ejemplo de ruta
                break;
            default:
                response.put("error", true);
                response.put("message", "Rol no reconocido");
                return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    public String register(RegisterRequest request) {
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
        return "Success";
    }

    public String registerMed(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Medico user = new Medico(
                request.getNombre(),
                request.getCorreo(),
                request.getTelefono(),
                request.getSexo(),
                encodedPassword,
                UserType.MEDICO
        );
        medicoRepository.save(user);
        return "Success";
    }

    public String registerAdmin(RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Medico user = new Medico(
                request.getNombre(),
                request.getCorreo(),
                request.getTelefono(),
                request.getSexo(),
                encodedPassword,
                UserType.ADMINISTRADOR
        );
        usuarioRepository.save(user);
        return "Success";
    }
}
