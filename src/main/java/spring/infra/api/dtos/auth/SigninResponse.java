package spring.infra.api.dtos.auth;

public record SigninResponse(
    String accessToken,
    Long expiresIn
) {}
