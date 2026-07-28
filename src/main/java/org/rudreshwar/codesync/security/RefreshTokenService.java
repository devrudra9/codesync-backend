package org.rudreshwar.codesync.security;

import lombok.RequiredArgsConstructor;
import org.rudreshwar.codesync.user.entity.User;
import org.rudreshwar.codesync.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String createRefreshTokenForUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return createForUser(user);
    }

    @Transactional
    public String createForUser(User user) {
        byte[] random = new byte[64];
        secureRandom.nextBytes(random);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        String hash = sha256Hex(token);
        Instant expiresAt = Instant.now().plusMillis(refreshExpirationMs);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHash(hash)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        refreshTokenRepository.save(rt);
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        if (token == null) return Optional.empty();
        String hash = sha256Hex(token);
        return refreshTokenRepository.findByTokenHash(hash);
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        return findByToken(token)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));
    }

    @Transactional
    public void revokeByToken(String token) {
        findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Scheduled(cron = "0 30 3 * * ?")
    @Transactional
    public void cleanupExpired() {
        refreshTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
