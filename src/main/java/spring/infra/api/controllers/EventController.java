package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.auth.SignupResponse;
import spring.infra.api.dtos.event.CreateEventRequest;
import spring.infra.api.dtos.event.CreateEventResponse;
import spring.infra.api.dtos.event.EventResponse;
import spring.infra.api.dtos.event.UpdateEventRequest;
import spring.infra.api.services.AuthService;
import spring.infra.api.services.EventService;
import spring.infra.api.services.UserService;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(
            @Valid
            @RequestBody
            CreateEventRequest createEventRequest,
            @AuthenticationPrincipal
            Jwt jwt
    ){
        UUID userId = UUID.fromString(jwt.getSubject());
        EventResponse response = this.eventService.createEvent(createEventRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = this.eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        EventResponse event = this.eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventRequest updateEventRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        EventResponse response = this.eventService.updateEvent(id, updateEventRequest, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        this.eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
}
