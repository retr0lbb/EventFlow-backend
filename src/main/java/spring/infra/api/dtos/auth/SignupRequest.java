package spring.infra.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "E-mail is required")
    @Email(message = "Invalid e-mail format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    String password,

    @NotBlank(message = "Name is required")
    String name,

    String phone
) {}
