package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.auth.SigninRequest;
import spring.infra.api.dtos.auth.SigninResponse;
import spring.infra.api.dtos.auth.SignupRequest;
import spring.infra.api.dtos.auth.SignupResponse;
import spring.infra.api.dtos.auth.TokenPair;
import spring.infra.api.services.AuthService;
import spring.infra.api.services.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService){
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = this.userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        TokenPair tokenPair = this.authService.signin(signinRequest);
        
        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(false) // Altere para true em ambiente de produção (requer HTTPS)
                .path("/api/v1/auth") // Restringe o cookie para ser enviado apenas nas rotas /auth
                .maxAge(86400) // 1 dia (24 horas)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new SigninResponse(tokenPair.accessToken(), tokenPair.expiresIn()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<SigninResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String oldRefreshToken) {
        
        if (oldRefreshToken == null || oldRefreshToken.isEmpty()) {
            throw new BadCredentialsException("Missing or expired refresh token cookie");
        }

        TokenPair tokenPair = this.authService.refresh(oldRefreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(false) // Altere para true em ambiente de produção (requer HTTPS)
                .path("/api/v1/auth")
                .maxAge(86400)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new SigninResponse(tokenPair.accessToken(), tokenPair.expiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        
        this.authService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(0) // Expira o cookie imediatamente
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
