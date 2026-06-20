package spring.infra.api.dtos.checkin;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCheckinRequest(
        @NotNull(message = "Event ID is required")
        UUID eventId
) {}
