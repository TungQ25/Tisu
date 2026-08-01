package com.example.tisu.service;

import java.util.List;
import java.util.UUID;

import com.example.tisu.dto.HabitRequest;
import com.example.tisu.dto.HabitResponse;
import com.example.tisu.entity.Habit;
import com.example.tisu.repository.HabitCompletionRepository;
import com.example.tisu.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;

    public HabitService(HabitRepository habitRepository, HabitCompletionRepository completionRepository) {
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
    }

    public List<HabitResponse> getHabits(String userId) {
        return habitRepository.findActiveAccessibleHabits(requireUserId(userId)).stream()
                .map(HabitResponse::from)
                .toList();
    }

    public HabitResponse createHabit(HabitRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        Habit habit = request.toEntity();
        if (habit.getId() == null || habit.getId().isBlank()) {
            habit.setId(UUID.randomUUID().toString());
        }

        if (habitRepository.existsById(habit.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Habit id already exists");
        }

        habit.setUserId(currentUserId);
        habit.setDeleted(false);
        habit.setDeletedAt(null);
        habit.setVersion(SyncMetadata.initialVersion());
        habit.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        normalizeTimestamps(habit, habit);
        return HabitResponse.from(habitRepository.save(habit));
    }

    public HabitResponse updateHabit(String id, HabitRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        Habit existing = habitRepository.findAccessibleById(id, currentUserId)
                .filter(habit -> !habit.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));

        SyncMetadata.requireFreshVersion(request.version(), existing.getVersion());
        Habit requestHabit = request.toEntity();
        request.applyTo(existing);
        normalizeTimestamps(existing, requestHabit);
        existing.setVersion(SyncMetadata.nextVersion(existing.getVersion()));
        existing.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        return HabitResponse.from(habitRepository.save(existing));
    }

    @Transactional
    public HabitResponse deleteHabit(String id, String deviceId, String userId) {
        String currentUserId = requireUserId(userId);
        Habit habit = habitRepository.findAccessibleById(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));
        if (habit.isDeleted()) {
            return HabitResponse.from(habit);
        }

        long now = System.currentTimeMillis();
        habit.setDeleted(true);
        habit.setDeletedAt(now);
        habit.setUpdatedAt(now);
        habit.setVersion(SyncMetadata.nextVersion(habit.getVersion()));
        habit.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(deviceId));
        completionRepository.markHabitCompletionsDeleted(id, currentUserId, now);
        return HabitResponse.from(habitRepository.save(habit));
    }

    private static void normalizeTimestamps(Habit target, Habit request) {
        long now = System.currentTimeMillis();
        if (target.getCreatedAt() <= 0) {
            target.setCreatedAt(request.getCreatedAt() > 0 ? request.getCreatedAt() : now);
        }
        target.setUpdatedAt(request.getUpdatedAt() > 0 ? request.getUpdatedAt() : now);
        target.setFrequency(normalizeFrequency(target.getFrequency()));
        target.setTotalDays(Math.max(0, target.getTotalDays()));
    }

    private static String normalizeFrequency(String frequency) {
        if ("weekly".equalsIgnoreCase(frequency)) {
            return "weekly";
        }
        if ("monthly".equalsIgnoreCase(frequency)) {
            return "monthly";
        }
        return "daily";
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userId;
    }
}
