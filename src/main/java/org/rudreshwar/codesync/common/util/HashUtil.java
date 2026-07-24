package org.rudreshwar.codesync.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public final class HashUtil {

    private HashUtil() {}

    public static String generateCommitHash(Long projectId, Long userId, String message) {
        try {
            String input = projectId + "|" + userId + "|" + message + "|" + LocalDateTime.now();
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate commit hash.", e);
        }
    }

}