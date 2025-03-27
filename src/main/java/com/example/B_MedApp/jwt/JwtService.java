package com.example.B_MedApp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    // La clave secreta para firmar el JWT, debe ser segura y preferentemente almacenada de forma segura.
    private static final String SECRET_KEY = "585c7ba334aaff7c49dc9493badeecb4d33b343f4df95f242a411a1debf1a971"; // Asegúrate de que esta clave sea segura

    // Obtener el token JWT a partir de los detalles del usuario
    public String getToken(UserDetails user) {

        //Para mandar el rol
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getAuthorities().iterator().next().getAuthority());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername()) // Seteamos el nombre del usuario como subject
                .setIssuedAt(new Date()) // Establecemos la fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Establecemos la fecha de expiración
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firmamos el token con nuestra clave
                .compact(); // Generamos el token JWT
    }

    // Obtener la clave secreta para firmar el JWT (convertida a Key)
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // Convertimos la clave secreta a un Key
    }

    // Obtener el nombre de usuario del token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject); // Extraemos el nombre de usuario del token
    }

    public String getRoleFromToken(String token) {
        return getClaim(token, claims -> claims.get("role", String.class));
    }

    // Validar si el token es válido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token); // Extraemos el nombre de usuario del token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Verificamos si el token es válido
    }

    // Obtener una reclamación (claim) del token
    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token); // Obtenemos todas las reclamaciones del token
        return claimsResolver.apply(claims); // Aplicamos la función que extrae el valor deseado
    }

    // Obtener todas las reclamaciones del token
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // Usamos la misma clave para verificar el token
                .build()
                .parseClaimsJws(token) // Parseamos el token
                .getBody(); // Extraemos las reclamaciones
    }

    // Verificar si el token ha expirado
    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date()); // Comprobamos si la fecha de expiración es anterior a la fecha actual
    }

    // Obtener la fecha de expiración del token
    private Date getExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration); // Extraemos la fecha de expiración del token
    }
}
