package com.example.B_MedApp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    //Podemos colocar la que queramos
    private String SECRET_KEY = "MiClaveSecretaDe256BitsMiClaveSecretaDe256Bits";


    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> claims, UserDetails user) {
        return Jwts
                .builder()
                .setClaims(claims)
                //getUsername lo definimos en el modelo usuario
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Decoders
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //Crea instancia de nuestra secret key
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    public String getUsernameFromToken(String token) {
//        return getClaim(token, Claims::getSubject);
//    }
//
//    //Comprobación
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = getUsernameFromToken(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private Claims getAllClaims(String token) {
//        return Jwts
//                .parserBuilder()
//                .setSigningKey(getKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    //Acceder a la expiración del token
//    private Date getIssuedAt(String token) {
//        return getClaim(token, Claims::getIssuedAt);
//    }
//
//    //Comprobar si expiro
//    private boolean isTokenExpired(String token) {
//        return getIssuedAt(token).before(new Date());
//    }

}
