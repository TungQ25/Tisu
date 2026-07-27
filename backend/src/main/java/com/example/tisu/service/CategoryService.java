package com.example.tisu.service;

import java.util.List;
import java.util.UUID;

import com.example.tisu.dto.CategoryRequest;
import com.example.tisu.dto.CategoryResponse;
import com.example.tisu.entity.Category;
import com.example.tisu.repository.CategoryRepository;
import com.example.tisu.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    public List<CategoryResponse> getCategories(String userId) {
        return categoryRepository.findVisibleAccessibleCategories(requireUserId(userId)).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse createCategory(CategoryRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        Category category = request.toEntity();
        if (category.getId() == null || category.getId().isBlank()) {
            category.setId(UUID.randomUUID().toString());
        }
        if (categoryRepository.existsById(category.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category id already exists");
        }
        category.setUserId(currentUserId);
        category.setDeleted(false);
        category.setDeletedAt(null);
        category.setVersion(SyncMetadata.initialVersion(request.version()));
        category.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        normalizeTimestamps(category, category);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(String id, CategoryRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        Category existing = categoryRepository.findAccessibleById(id, currentUserId)
                .filter(category -> !category.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        SyncMetadata.requireFreshVersion(request.version(), existing.getVersion());
        Category requestedCategory = request.toEntity();
        request.applyTo(existing);
        normalizeTimestamps(existing, requestedCategory);
        existing.setVersion(SyncMetadata.nextVersion(existing.getVersion()));
        existing.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        return CategoryResponse.from(categoryRepository.save(existing));
    }

    @Transactional
    public CategoryResponse deleteCategory(String id, String deviceId, String userId) {
        String currentUserId = requireUserId(userId);
        Category category = categoryRepository.findAccessibleById(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        long now = System.currentTimeMillis();
        category.setDeleted(true);
        category.setDeletedAt(now);
        category.setPinned(false);
        category.setPinnedOrder(-1);
        category.setUpdatedAt(now);
        category.setVersion(SyncMetadata.nextVersion(category.getVersion()));
        category.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(deviceId));
        taskRepository.markCategoryTasksDeleted(id, currentUserId, now, now);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    /**
     * Chuẩn hóa thời gian tạo và thời gian cập nhật
     * @param target
     * @param request
     */
    private static void normalizeTimestamps(Category target, Category request) {
        long now = System.currentTimeMillis();
        // Chưa có tgian tạo thì gán
        if (target.getCreatedAt() <= 0) {
            target.setCreatedAt(request.getCreatedAt() > 0 ? request.getCreatedAt() : now);
        }
        // Luôn gán lại tgian cập nhật
        target.setUpdatedAt(request.getUpdatedAt() > 0 ? request.getUpdatedAt() : now);
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userId;
    }
}
