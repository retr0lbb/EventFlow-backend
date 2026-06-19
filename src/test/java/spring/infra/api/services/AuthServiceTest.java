package spring.infra.api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.infra.api.dtos.auth.SigninRequest;
import spring.infra.api.dtos.auth.TokenPair;
import spring.infra.api.models.User;
import spring.infra.api.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("adrian@test.com");
        user.setPasswd("encodedPassword");
    }

    @Test
    void signin_WithValidCredentials_ShouldReturnTokenPair() {
        // Arrange
        SigninRequest request = new SigninRequest("adrian@test.com", "password123");
        
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.passwd(), user.getPasswd())).thenReturn(true);
        when(tokenService.generateAccessToken(user)).thenReturn("accessTokenJWT");
        when(tokenService.generateRefreshToken(userId)).thenReturn("refreshTokenUUID");

        // Act
        TokenPair response = authService.signin(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessTokenJWT", response.accessToken());
        assertEquals("refreshTokenUUID", response.refreshToken());
        assertEquals(300L, response.expiresIn());

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(passwordEncoder, times(1)).matches(request.passwd(), user.getPasswd());
        verify(tokenService, times(1)).generateAccessToken(user);
        verify(tokenService, times(1)).generateRefreshToken(userId);
    }

    @Test
    void signin_WithNonExistentEmail_ShouldThrowBadCredentialsException() {
        // Arrange
        SigninRequest request = new SigninRequest("nonexistent@test.com", "password123");
        
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.signin(request));
        
        verify(userRepository, times(1)).findByEmail(request.email());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void signin_WithIncorrectPassword_ShouldThrowBadCredentialsException() {
        // Arrange
        SigninRequest request = new SigninRequest("adrian@test.com", "wrongpassword");
        
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.passwd(), user.getPasswd())).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.signin(request));
        
        verify(userRepository, times(1)).findByEmail(request.email());
        verify(passwordEncoder, times(1)).matches(request.passwd(), user.getPasswd());
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void refresh_WithValidToken_ShouldRotateTokens() {
        // Arrange
        String oldToken = "oldRefreshTokenUUID";
        
        when(tokenService.getUserIdFromRefreshToken(oldToken)).thenReturn(userId.toString());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(user)).thenReturn("newAccessTokenJWT");
        when(tokenService.generateRefreshToken(userId)).thenReturn("newRefreshTokenUUID");

        // Act
        TokenPair response = authService.refresh(oldToken);

        // Assert
        assertNotNull(response);
        assertEquals("newAccessTokenJWT", response.accessToken());
        assertEquals("newRefreshTokenUUID", response.refreshToken());
        
        verify(tokenService, times(1)).getUserIdFromRefreshToken(oldToken);
        verify(tokenService, times(1)).revokeRefreshToken(oldToken);
        verify(userRepository, times(1)).findById(userId);
        verify(tokenService, times(1)).generateAccessToken(user);
        verify(tokenService, times(1)).generateRefreshToken(userId);
    }

    @Test
    void refresh_WithMissingToken_ShouldThrowBadCredentialsException() {
        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.refresh(null));
        assertThrows(BadCredentialsException.class, () -> authService.refresh(""));
        
        verify(tokenService, never()).getUserIdFromRefreshToken(any());
    }

    @Test
    void logout_WithValidToken_ShouldRevokeToken() {
        // Arrange
        String token = "activeTokenUUID";

        // Act
        authService.logout(token);

        // Assert
        verify(tokenService, times(1)).revokeRefreshToken(token);
    }
}
