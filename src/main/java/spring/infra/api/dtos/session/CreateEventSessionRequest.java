package spring.infra.api.dtos.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.UUID;

public record CreateEventSessionRequest(
        @NotNull(message = "Event ID is required")
        UUID eventId,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Start time is required")
        Timestamp startsAt,

        @NotNull(message = "End time is required")
        Timestamp endsAt,

        Integer capacity
) {}
