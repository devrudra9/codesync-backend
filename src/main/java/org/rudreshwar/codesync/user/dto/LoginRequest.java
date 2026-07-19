package org.rudreshwar.codesync.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    // To accept both username or email
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
