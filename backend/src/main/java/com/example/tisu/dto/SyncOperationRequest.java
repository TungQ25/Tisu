package com.example.tisu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.tisu.entity.SyncEntityType;
import com.example.tisu.entity.SyncOperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SyncOperationRequest(
        @NotBlank
        String deviceId,

        @NotNull
        SyncEntityType entityType,

        @NotBlank
        String entityId,

        @NotNull
        SyncOperationType operation,

        @PositiveOrZero
        long baseVersion,

        @PositiveOrZero
        long clientTimestamp,
        String payloadJson
) {
}
