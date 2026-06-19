package spring.infra.api.dtos.session;

import java.sql.Timestamp;
import java.util.UUID;

public record EventSessionResponse(
        Long id,
        UUID eventId,
        String title,
        String description,
        Timestamp startsAt,
        Timestamp endsAt,
        Integer capacity,
        Long createdAt,
        Long updatedAt
) {}
