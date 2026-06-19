package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.session.CreateEventSessionRequest;
import spring.infra.api.dtos.session.EventSessionResponse;
import spring.infra.api.dtos.session.UpdateEventSessionRequest;
import spring.infra.api.services.EventSessionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event-session")
public class EventSessionController {

    private final EventSessionService  eventSessionService;

    public EventSessionController(EventSessionService eventSessionService) {
        this.eventSessionService = eventSessionService;
    }

    @PostMapping("/create")
    public ResponseEntity<EventSessionResponse> createSession(
            @Valid @RequestBody CreateEventSessionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        EventSessionResponse response = eventSessionService.createSession(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventSessionResponse>> getSessionsByEvent(@PathVariable UUID eventId) {
        List<EventSessionResponse> sessions = eventSessionService.getAllSessionsByEvent(eventId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventSessionResponse> getSessionById(@PathVariable Long id) {
        EventSessionResponse session = eventSessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventSessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventSessionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        EventSessionResponse response = eventSessionService.updateSession(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        eventSessionService.deleteSession(id, userId);
        return ResponseEntity.noContent().build();
    }
}
