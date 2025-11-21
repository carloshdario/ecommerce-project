package br.com.datasinapse.ecommerce.util;

import br.com.datasinapse.ecommerce.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;
import java.time.temporal.ChronoUnit;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("SECRET JWT NÃO CONFIGURADO!");
        }

        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }


    public String generateToken(String username, Role role) {

        String roleName = role.name().startsWith("ROLE_") ?
                role.name() : "ROLE_" + role.name();

        Instant now = Instant.now();

        return JWT.create()
                .withSubject(username)
                .withClaim("role", roleName)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(1, ChronoUnit.DAYS)))
                .sign(algorithm);
    }


    public DecodedJWT validate(String token) {
        return verifier.verify(token);
    }


    public String extractUsername(String token) {
        return validate(token).getSubject();
    }

    public String extractRole(String token) {
        return validate(token).getClaim("role").asString();
    }
}
