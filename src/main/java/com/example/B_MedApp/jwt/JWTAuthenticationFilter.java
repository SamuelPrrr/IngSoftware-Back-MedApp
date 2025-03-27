package com.example.B_MedApp.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

// Extiende de esa clase abstracta para crear filtros personalizados
// y garantizar que el filtro se ejecute solo una vez por cada solicitud HTTPS
@Component
@RequiredArgsConstructor // Inyecta las dependencias automáticamente
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Filtros relacionados con el token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Ignorar la ruta de registro
        // **Cambio realizado**: Aseguramos que no se procese el token cuando la ruta sea /register
        if (request.getRequestURI().equals("/register")) {
            filterChain.doFilter(request, response); // Pasamos al siguiente filtro
            return;
        }

        final String token = getTokenFromRequest(request);

        // **Cambio realizado**: Si el token es nulo, no se hace nada y continúa el flujo en caso del registro
        if (token == null) {
            filterChain.doFilter(request, response); // Si no hay token, se pasa al siguiente filtro
            return;
        }

        // **Cambio realizado**: El código ahora obtiene el username solo si el token no es nulo o sea para el login
        final String username = jwtService.getUsernameFromToken(token);

        // **Cambio realizado**: Validamos que el username no sea null antes de hacer la búsqueda
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                // Obtener el rol desde el token
                String role = jwtService.getRoleFromToken(token);

                // 🔹 Imprimir para depuración
                System.out.println("Rol extraído del token: " + role);
                System.out.println("Roles en UserDetails: " + userDetails.getAuthorities());

                // Asegurar que el rol tenga el prefijo ROLE_
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                Collection<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                // Si el token es válido, se establece la autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        //Por defecto:
                        //userDetails.getAuthorities()
                        authorities // ✅ Ahora se usa el rol correcto
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos el filtro
        filterChain.doFilter(request, response);
    }

    // Método para obtener el token de la cabecera de la solicitud
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si el encabezado de autorización está presente y comienza con "Bearer ", extraemos el token
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Extraemos el token
        }
        return null; // Si no hay encabezado válido, retornamos null
    }
}
