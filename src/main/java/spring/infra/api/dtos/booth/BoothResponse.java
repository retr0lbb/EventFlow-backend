package spring.infra.api.dtos.booth;

import java.util.UUID;

public record BoothResponse(
        Long id,
        UUID eventId,
        String name,
        String description,
        String mapLocation,
        Integer capacity
) {}
