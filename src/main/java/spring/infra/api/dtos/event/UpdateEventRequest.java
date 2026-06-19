package spring.infra.api.dtos.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEventRequest(
        @NotBlank(message = "The event name is required")
        @Size(max = 50, min = 3, message = "The event name must be between 3 and 50 characters long")
        String name,

        @NotBlank(message = "The CNPJ is required")
        @Size(min = 14, max = 14, message = "The CNPJ must be 14 characters long")
        String cnpj,

        @Size(min = 10, max = 255, message = "The event description must be between 10 and 255 characters long.")
        String description,

        String address,
        Double latitude,
        Double longitude,
        Integer maxParticipants,
        Integer startsAt,
        Integer endsAt
) {}
