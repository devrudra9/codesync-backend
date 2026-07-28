package org.rudreshwar.codesync.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Transactional
    public void blacklistToken(String token, Instant expiresAt) {
        if (token == null || token.isBlank()) return;
        if (!jwtBlacklistRepository.existsByToken(token)) {
            JwtBlacklist entry = JwtBlacklist.builder()
                    .token(token)
                    .expiresAt(expiresAt)
                    .build();
            jwtBlacklistRepository.save(entry);
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null) return false;
        return jwtBlacklistRepository.existsByToken(token);
    }

    // periodic cleanup of expired blacklist entries (runs daily)
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpired() {
        jwtBlacklistRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}
