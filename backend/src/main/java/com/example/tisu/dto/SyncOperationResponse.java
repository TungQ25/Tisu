package com.example.tisu.dto;

import com.example.tisu.entity.SyncOperation;
import com.example.tisu.entity.SyncEntityType;
import com.example.tisu.entity.SyncOperationType;

public record SyncOperationResponse(
        String id,
        String userId,
        String deviceId,
        SyncEntityType entityType,
        String entityId,
        SyncOperationType operation,
        long baseVersion,
        long clientTimestamp,
        long serverTimestamp,
        String payloadJson
) {
    public static SyncOperationResponse from(SyncOperation operation) {
        return new SyncOperationResponse(
                operation.getId(),
                operation.getUserId(),
                operation.getDeviceId(),
                operation.getEntityType(),
                operation.getEntityId(),
                operation.getOperation(),
                operation.getBaseVersion(),
                operation.getClientTimestamp(),
                operation.getServerTimestamp(),
                operation.getPayloadJson()
        );
    }
}
