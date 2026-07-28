package org.rudreshwar.codesync.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.security.JwtTokenProvider;
import org.rudreshwar.codesync.security.TokenBlacklistService;
import org.rudreshwar.codesync.user.dto.LoginRequest;
import org.rudreshwar.codesync.user.dto.SignupRequest;
import org.rudreshwar.codesync.user.dto.AuthResponse;
import org.rudreshwar.codesync.user.service.UserService;
import org.rudreshwar.codesync.security.RefreshTokenService;
import org.rudreshwar.codesync.security.CustomUserDetailsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthResponse auth = authService.signup(signupRequest);
        String refreshToken = refreshTokenService.createRefreshTokenForUserId(auth.getUserId());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(604800)
                .sameSite("Lax")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(auth);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse auth = authService.login(loginRequest);
        String refreshToken = refreshTokenService.createRefreshTokenForUserId(auth.getUserId());
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(604800)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(auth);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshTokenCookie) {
        if (refreshTokenCookie == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token missing"));
        }
        try {
            var rt = refreshTokenService.verifyRefreshToken(refreshTokenCookie);
            UserDetails userDetails = customUserDetailsService.loadUserById(rt.getUser().getId());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = jwtTokenProvider.generateToken(authentication);

            // rotate refresh token
            refreshTokenService.revokeByToken(refreshTokenCookie);
            String newRefresh = refreshTokenService.createRefreshTokenForUserId(rt.getUser().getId());
            ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefresh)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(604800)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("accessToken", newAccessToken));

        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                                    @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            java.util.Date exp = jwtTokenProvider.getExpirationDateFromToken(token);
            java.time.Instant expiresAt = (exp != null) ? exp.toInstant() : java.time.Instant.now();
            tokenBlacklistService.blacklistToken(token, expiresAt);
        }

        if (refreshTokenCookie != null) {
            refreshTokenService.revokeByToken(refreshTokenCookie);
        }

        // clear cookie
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(Map.of("message", "Logged out"));
    }

}

