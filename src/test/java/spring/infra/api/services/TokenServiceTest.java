package spring.infra.api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;
import spring.infra.api.models.Role;
import spring.infra.api.models.User;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenService tokenService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("adrian@test.com");

        Role role = new Role();
        role.setRoleId(1L);
        role.setName("VISITANT");
        user.setRoles(Collections.singleton(role));

        // Injetar valores de expiração via ReflectionTestUtils do Spring Test
        ReflectionTestUtils.setField(tokenService, "accessTokenExpiration", 300L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenExpiration", 86400L);
    }

    @Test
    void generateAccessToken_ShouldReturnSignedJwtWithRoles() {
        // Arrange
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn("mockedJwtTokenString");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = tokenService.generateAccessToken(user);

        // Assert
        assertNotNull(token);
        assertEquals("mockedJwtTokenString", token);

        ArgumentCaptor<JwtEncoderParameters> argument = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder, times(1)).encode(argument.capture());
        
        JwtEncoderParameters params = argument.getValue();
        assertNotNull(params);
        assertTrue(params.getClaims().getClaims().containsKey("scope"));
        assertEquals("VISITANT", params.getClaims().getClaim("scope"));
        assertEquals(userId.toString(), params.getClaims().getSubject());
    }

    @Test
    void generateRefreshToken_ShouldWriteToRedisWithTTL() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        String token = tokenService.generateRefreshToken(userId);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(
                eq("refresh_token:" + token),
                eq(userId.toString()),
                eq(86400L),
                eq(TimeUnit.SECONDS)
        );
    }

    @Test
    void getUserIdFromRefreshToken_WithValidToken_ShouldReturnUserId() {
        // Arrange
        String token = "validTokenUUID";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + token)).thenReturn(userId.toString());

        // Act
        String result = tokenService.getUserIdFromRefreshToken(token);

        // Assert
        assertNotNull(result);
        assertEquals(userId.toString(), result);
        
        verify(valueOperations, times(1)).get("refresh_token:" + token);
    }

    @Test
    void getUserIdFromRefreshToken_WithInvalidToken_ShouldThrowBadCredentialsException() {
        // Arrange
        String token = "invalidTokenUUID";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh_token:" + token)).thenReturn(null);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> tokenService.getUserIdFromRefreshToken(token));
        
        verify(valueOperations, times(1)).get("refresh_token:" + token);
    }

    @Test
    void revokeRefreshToken_WithValidToken_ShouldDeleteFromRedis() {
        // Arrange
        String token = "tokenToDelete";

        // Act
        tokenService.revokeRefreshToken(token);

        // Assert
        verify(redisTemplate, times(1)).delete("refresh_token:" + token);
    }
}
