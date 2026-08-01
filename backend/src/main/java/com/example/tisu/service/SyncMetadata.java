package com.example.tisu.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SyncMetadata {
    private SyncMetadata() {
    }

    public static void requireFreshVersion(long requestedVersion, long currentVersion) {
        if (requestedVersion != currentVersion) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Version conflict");
        }
    }

    public static long initialVersion() {
        return 1L;
    }

    public static long nextVersion(long currentVersion) {
        return currentVersion > 0 ? currentVersion + 1L : 1L;
    }

    public static String normalizeDeviceId(String deviceId) {
        return deviceId == null || deviceId.isBlank() ? null : deviceId.trim();
    }
}
