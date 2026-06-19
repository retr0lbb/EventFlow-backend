package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.auth.SignupResponse;
import spring.infra.api.dtos.event.CreateEventRequest;
import spring.infra.api.dtos.event.CreateEventResponse;
import spring.infra.api.services.AuthService;
import spring.infra.api.services.EventService;
import spring.infra.api.services.UserService;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {

    private EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateEventResponse> createEvent(
            @Valid
            @RequestBody
            CreateEventRequest createEventRequest
    ){
        CreateEventResponse response = this.eventService.createEvent(createEventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
