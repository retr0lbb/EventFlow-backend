package spring.infra.api.dtos.session;

import java.sql.Timestamp;

public record BoothSessionResponse(
        Long id,
        Long boothId,
        Long eventSessionId,
        String title,
        String description,
        Timestamp startsAt,
        Timestamp endsAt,
        Integer capacity
) {}
