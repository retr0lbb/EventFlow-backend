package spring.infra.api.dtos.event;

import spring.infra.api.enums.EventStatus;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String name,
        String cnpj,
        String description,
        String address,
        Double latitude,
        Double longitude,
        Integer maxParticipants,
        Integer startsAt,
        Integer endsAt,
        EventStatus status,
        Long createdAt
) {}
