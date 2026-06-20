package spring.infra.api.dtos.event;

import spring.infra.api.enums.EventStatus;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        String description,
        String address,
        Integer maxParticipants,
        String startsAt,
        String endsAt,
        EventStatus status,
        String createdAt
) {}