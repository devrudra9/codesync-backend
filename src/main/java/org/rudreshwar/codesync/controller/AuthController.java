package org.rudreshwar.codesync.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.dto.request.LoginRequest;
import org.rudreshwar.codesync.dto.request.SignupRequest;
import org.rudreshwar.codesync.dto.response.AuthResponse;
import org.rudreshwar.codesync.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

}
