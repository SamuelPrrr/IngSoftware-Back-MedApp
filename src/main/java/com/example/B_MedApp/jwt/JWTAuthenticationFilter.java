package com.example.B_MedApp.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


//Extiende de esa clase abstracta para crear filtros personalizados
// y garantizar que el filtro se ejecute solo una vez por cada solicitud HTTPS
@Component
@RequiredArgsConstructor //Inyecta las dependencias automaticamente
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    //Le mandamos nuestro servicio para que detecte nuestro token (LOGIN)
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;

    //Filtros relacionados con el token
    //filterChain es la cadena de filtros que hicimos en SecurityConfig
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String token = getTokenFromRequest(request);
//        final String username; (login)

        if (token != null) {
            filterChain.doFilter(request, response);
            return;
        }

//        username = jwtService.getUsernameFromToken(token);

        //Si no encontramos el username lo vamos a buscar a la BD (LOGIN)
//        if(username == null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            if(jwtService.isTokenValid(token, userDetails)){
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities());
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }

        filterChain.doFilter(request, response); 
    }




    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
