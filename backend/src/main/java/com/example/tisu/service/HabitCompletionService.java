package com.example.tisu.service;

import java.util.List;
import java.util.UUID;

import com.example.tisu.dto.HabitCompletionRequest;
import com.example.tisu.dto.HabitCompletionResponse;
import com.example.tisu.entity.HabitCompletion;
import com.example.tisu.repository.HabitCompletionRepository;
import com.example.tisu.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HabitCompletionService {

    private final HabitCompletionRepository completionRepository;
    private final HabitRepository habitRepository;

    public HabitCompletionService(
            HabitCompletionRepository completionRepository,
            HabitRepository habitRepository
    ) {
        this.completionRepository = completionRepository;
        this.habitRepository = habitRepository;
    }

    public List<HabitCompletionResponse> getCompletions(String userId) {
        return completionRepository.findActiveAccessibleCompletions(requireUserId(userId)).stream()
                .map(HabitCompletionResponse::from)
                .toList();
    }

    public HabitCompletionResponse createCompletion(HabitCompletionRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        HabitCompletion completion = request.toEntity();
        if (completion.getId() == null || completion.getId().isBlank()) {
            completion.setId(UUID.randomUUID().toString());
        }

        // Kiểm tra completion đã tồn tại hay chưa, rồi thì restore trạng thái completion thay vì tạo mới
        HabitCompletion existingCompletion = completionRepository
                .findAccessibleById(completion.getId(), currentUserId)
                .orElse(null);
        if (existingCompletion != null) {
            return restoreCompletion(existingCompletion, completion, request, currentUserId);
        }

        if (completionRepository.existsById(completion.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Habit completion id already exists");
        }

        validateHabit(completion.getHabitId(), currentUserId);
        completion.setUserId(currentUserId);
        completion.setDeleted(false);
        completion.setDeletedAt(null);
        completion.setVersion(SyncMetadata.initialVersion(request.version()));
        completion.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        normalizeTimestamps(completion, completion);
        return HabitCompletionResponse.from(completionRepository.save(completion));
    }

    public HabitCompletionResponse updateCompletion(String id, HabitCompletionRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        HabitCompletion existing = completionRepository.findAccessibleById(id, currentUserId)
                .filter(completion -> !completion.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit completion not found"));

        SyncMetadata.requireFreshVersion(request.version(), existing.getVersion());
        HabitCompletion requestedCompletion = request.toEntity();
        validateHabit(requestedCompletion.getHabitId(), currentUserId);
        request.applyTo(existing);
        normalizeTimestamps(existing, requestedCompletion);
        existing.setVersion(SyncMetadata.nextVersion(existing.getVersion()));
        existing.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        return HabitCompletionResponse.from(completionRepository.save(existing));
    }

    public HabitCompletionResponse deleteCompletion(String id, String deviceId, String userId) {
        String currentUserId = requireUserId(userId);
        HabitCompletion completion = completionRepository.findAccessibleById(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit completion not found"));
        if (completion.isDeleted()) {
            return HabitCompletionResponse.from(completion);
        }

        long now = System.currentTimeMillis();
        completion.setDeleted(true);
        completion.setDeletedAt(now);
        completion.setUpdatedAt(now);
        completion.setVersion(SyncMetadata.nextVersion(completion.getVersion()));
        completion.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(deviceId));
        return HabitCompletionResponse.from(completionRepository.save(completion));
    }

    private HabitCompletionResponse restoreCompletion(
            HabitCompletion existingCompletion,
            HabitCompletion requestedCompletion,
            HabitCompletionRequest request,
            String userId
    ) {
        if (!existingCompletion.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Habit completion id already exists");
        }

        SyncMetadata.requireFreshVersion(request.version(), existingCompletion.getVersion());
        validateHabit(requestedCompletion.getHabitId(), userId);
        request.applyTo(existingCompletion);
        existingCompletion.setDeleted(false);
        existingCompletion.setDeletedAt(null);
        existingCompletion.setVersion(SyncMetadata.nextVersion(existingCompletion.getVersion()));
        existingCompletion.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        normalizeTimestamps(existingCompletion, requestedCompletion);
        return HabitCompletionResponse.from(completionRepository.save(existingCompletion));
    }

    private void validateHabit(String habitId, String userId) {
        if (habitId == null || habitId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Habit id is required");
        }
        habitRepository.findAccessibleById(habitId, userId)
                .filter(habit -> !habit.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Habit not found"));
    }

    private static void normalizeTimestamps(HabitCompletion target, HabitCompletion request) {
        long now = System.currentTimeMillis();
        if (target.getCompletedAt() <= 0) {
            target.setCompletedAt(request.getCompletedAt() > 0 ? request.getCompletedAt() : now);
        }
        target.setUpdatedAt(request.getUpdatedAt() > 0 ? request.getUpdatedAt() : now);
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userId;
    }
}
