package spring.infra.api.dtos.booth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateBoothRequest(
        @NotNull(message = "Event ID is required")
        UUID eventId,

        @NotBlank(message = "Booth name is required")
        @Size(min = 3, max = 100, message = "Booth name must be between 3 and 100 characters")
        String name,

        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        String mapLocation,

        Integer capacity
) {}
