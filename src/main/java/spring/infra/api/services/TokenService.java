package spring.infra.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import spring.infra.api.models.Role;
import spring.infra.api.models.User;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.access-token.expiration-seconds}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-seconds}")
    private Long refreshTokenExpiration;

    public TokenService(JwtEncoder jwtEncoder, StringRedisTemplate redisTemplate) {
        this.jwtEncoder = jwtEncoder;
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        
        // Mapear roles como escopos separados por espaço (padrão OAuth2/Spring Security)
        String scopes = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("EventFlow")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiration))
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(UUID userId) {
        String token = UUID.randomUUID().toString();
        String redisKey = "refresh_token:" + token;

        // Salvar no Redis: chave "refresh_token:<UUID>" -> valor "<userId>"
        redisTemplate.opsForValue().set(
                redisKey,
                userId.toString(),
                refreshTokenExpiration,
                TimeUnit.SECONDS
        );

        return token;
    }

    public String getUserIdFromRefreshToken(String token) {
        String redisKey = "refresh_token:" + token;
        String userId = redisTemplate.opsForValue().get(redisKey);
        
        if (userId == null) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        
        return userId;
    }

    public void revokeRefreshToken(String token) {
        String redisKey = "refresh_token:" + token;
        redisTemplate.delete(redisKey);
    }
}
