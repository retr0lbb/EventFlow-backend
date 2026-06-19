package spring.infra.api.dtos.auth;

import java.util.UUID;

public record SignupResponse(
    UUID id,
    String email,
    String name,
    String phone
) {}
