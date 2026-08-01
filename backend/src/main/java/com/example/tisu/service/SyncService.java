package com.example.tisu.service;

import com.example.tisu.dto.SyncOperationRequest;
import com.example.tisu.dto.SyncOperationResponse;
import com.example.tisu.entity.SyncOperation;
import com.example.tisu.repository.SyncOperationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SyncService {
    private final SyncOperationRepository syncOperationRepository;

    public SyncService(SyncOperationRepository syncOperationRepository) {
        this.syncOperationRepository = syncOperationRepository;
    }

    @Transactional(readOnly = true)
    public List<SyncOperationResponse> getOperations(long since, String userId) {
        return syncOperationRepository
                .findByUserIdAndServerTimestampGreaterThanOrderByServerTimestampAsc(requireUserId(userId), since)
                .stream()
                .map(SyncOperationResponse::from)
                .toList();
    }

    @Transactional
    public SyncOperationResponse createOperation(SyncOperationRequest request, String userId) {
        long now = System.currentTimeMillis();
        SyncOperation operation = new SyncOperation();
        operation.setId(UUID.randomUUID().toString());
        operation.setUserId(requireUserId(userId));
        operation.setDeviceId(request.deviceId().trim());
        operation.setEntityType(request.entityType());
        operation.setEntityId(request.entityId().trim());
        operation.setOperation(request.operation());
        operation.setBaseVersion(Math.max(0L, request.baseVersion()));
        operation.setClientTimestamp(request.clientTimestamp() > 0 ? request.clientTimestamp() : now);
        operation.setServerTimestamp(now);
        operation.setPayloadJson(request.payloadJson());
        return SyncOperationResponse.from(syncOperationRepository.save(operation));
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userId;
    }
}
