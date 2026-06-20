package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.checkin.CheckinResponse;
import spring.infra.api.dtos.checkin.CreateCheckinRequest;
import spring.infra.api.services.CheckinService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkin")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @PostMapping
    public ResponseEntity<CheckinResponse> createCheckin(
            @Valid @RequestBody CreateCheckinRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        CheckinResponse response = checkinService.createCheckin(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinResponse> validateCheckin(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        CheckinResponse response = checkinService.validateCheckin(id, userId);
        return ResponseEntity.ok(response);
    }
}
