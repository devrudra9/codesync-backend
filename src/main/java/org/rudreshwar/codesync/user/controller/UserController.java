package org.rudreshwar.codesync.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.user.dto.LoginRequest;
import org.rudreshwar.codesync.user.dto.SignupRequest;
import org.rudreshwar.codesync.user.dto.AuthResponse;
import org.rudreshwar.codesync.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Authorization header missing or invalid"));
        }
        String token = authorization.substring(7);
        java.util.Date exp = jwtTokenProvider.getExpirationDateFromToken(token);
        java.time.Instant expiresAt = (exp != null) ? exp.toInstant() : java.time.Instant.now();
        tokenBlacklistService.blacklistToken(token, expiresAt);
        return ResponseEntity.ok(java.util.Map.of("message", "Logged out"));
    }

}
