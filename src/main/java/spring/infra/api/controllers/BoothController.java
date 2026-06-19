package spring.infra.api.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import spring.infra.api.dtos.booth.BoothResponse;
import spring.infra.api.dtos.booth.CreateBoothRequest;
import spring.infra.api.dtos.booth.UpdateBoothRequest;
import spring.infra.api.services.BoothService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booth")
public class BoothController {

    private final BoothService boothService;

    public BoothController(BoothService boothService) {
        this.boothService = boothService;
    }

    @PostMapping("/create")
    public ResponseEntity<BoothResponse> createBooth(
            @Valid @RequestBody CreateBoothRequest createBoothRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        BoothResponse response = boothService.createBooth(createBoothRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BoothResponse>> getBoothsByEvent(@PathVariable UUID eventId) {
        List<BoothResponse> booths = boothService.getAllBoothsByEvent(eventId);
        return ResponseEntity.ok(booths);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoothResponse> getBoothById(@PathVariable Long id) {
        BoothResponse booth = boothService.getBoothById(id);
        return ResponseEntity.ok(booth);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoothResponse> updateBooth(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBoothRequest updateBoothRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        BoothResponse response = boothService.updateBooth(id, updateBoothRequest, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooth(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boothService.deleteBooth(id, userId);
        return ResponseEntity.noContent().build();
    }
}
