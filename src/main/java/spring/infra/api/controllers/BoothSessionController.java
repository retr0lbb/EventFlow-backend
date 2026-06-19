package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.session.BoothSessionResponse;
import spring.infra.api.dtos.session.CreateBoothSessionRequest;
import spring.infra.api.dtos.session.UpdateBoothSessionRequest;
import spring.infra.api.services.BoothSessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booth-session")
public class BoothSessionController {

    private final BoothSessionService boothSessionService;

    public BoothSessionController(BoothSessionService boothSessionService) {
        this.boothSessionService = boothSessionService;
    }

    @PostMapping("/create")
    public ResponseEntity<BoothSessionResponse> createSession(
            @Valid @RequestBody CreateBoothSessionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        BoothSessionResponse response = boothSessionService.createSession(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/booth/{boothId}")
    public ResponseEntity<List<BoothSessionResponse>> getSessionsByBooth(@PathVariable Long boothId) {
        List<BoothSessionResponse> sessions = boothSessionService.getAllSessionsByBooth(boothId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoothSessionResponse> getSessionById(@PathVariable Long id) {
        BoothSessionResponse session = boothSessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoothSessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoothSessionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        BoothSessionResponse response = boothSessionService.updateSession(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boothSessionService.deleteSession(id, userId);
        return ResponseEntity.noContent().build();
    }
}
