package spring.infra.api.dtos.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(
        @NotBlank(message = "The event name is required")
        @Size(max = 50, min = 3, message = "The event name must be between 3 and 50 characters long")
        String name,

        @Size(min = 10, max = 255, message = "The event name must be between 10 and 255 characters long.")
        String description,

        String address,
        Integer maxParticipants,

        @NotBlank(message = "The start date is required")
        String startsAt,

        @NotBlank(message = "The end date is required")
        String endsAt,

        String bannerUrl
) {
    public CreateEventRequest {
        if (maxParticipants == null) {
            maxParticipants = 100;
        }
        if(description != null && description.strip().isEmpty()){
            description = null;
        }
    }
}