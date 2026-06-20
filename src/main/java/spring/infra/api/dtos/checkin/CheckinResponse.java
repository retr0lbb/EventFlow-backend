package spring.infra.api.dtos.checkin;

import spring.infra.api.enums.CheckinStatus;

import java.util.UUID;

public record CheckinResponse(
        UUID id,
        UUID eventId,
        UUID userId,
        CheckinStatus status,
        Long createdAt
) {}
