package spring.infra.api.dtos.auth;

public record TokenPair(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {}
