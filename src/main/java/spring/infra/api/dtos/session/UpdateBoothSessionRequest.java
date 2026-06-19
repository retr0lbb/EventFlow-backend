package spring.infra.api.dtos.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

public record UpdateBoothSessionRequest(
        Long eventSessionId,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Start time is required")
        Timestamp startsAt,

        @NotNull(message = "End time is required")
        Timestamp endsAt,

        Integer capacity
) {}
