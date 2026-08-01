package com.example.tisu.service;

import java.util.List;
import java.util.UUID;

import com.example.tisu.dto.TaskRequest;
import com.example.tisu.dto.TaskResponse;
import com.example.tisu.entity.Task;
import com.example.tisu.repository.CategoryRepository;
import com.example.tisu.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository, CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TaskResponse> getAllTasks(String userId) {
        return taskRepository.findActiveAccessibleTasks(requireUserId(userId)).stream()
                .map(TaskResponse::from)
                .toList();
    }

    public List<TaskResponse> getTrash(String userId) {
        return taskRepository.findAccessibleTrashTasks(requireUserId(userId)).stream()
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTaskById(String id, String userId) {
        return taskRepository.findAccessibleById(id, requireUserId(userId))
                .filter(task -> !task.isDeleted())
                .map(TaskResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public TaskResponse createTask(TaskRequest request, String userId) {
        String currentUserId = requireUserId(userId);
        Task task = request.toEntity();
        if (task.getId() == null || task.getId().isBlank()) {
            task.setId(UUID.randomUUID().toString());
        }
        if (taskRepository.existsById(task.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task id already exists");
        }
        normalizeOptionalFields(task);
        validateCategory(task.getCategoryId(), currentUserId);
        task.setDeleted(false);
        task.setDeletedAt(null);
        task.setUserId(currentUserId);
        task.setVersion(SyncMetadata.initialVersion());
        task.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        normalizeUpdatedAt(task, task);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse updateTask(String id, TaskRequest request, String userId) {
        String currentUserId = requireUserId(userId);
            Task existingTask = taskRepository.findAccessibleById(id, currentUserId)
                .filter(task -> !task.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        SyncMetadata.requireFreshVersion(request.version(), existingTask.getVersion()); // Kiểm tra phiên bản đồng bộ

        Task requestTask = request.toEntity();
        normalizeOptionalFields(requestTask); // lần 1 kiểm tra category và dùng dữ liệu tạm đã làm sạch
        validateCategory(requestTask.getCategoryId(), currentUserId);

        request.applyTo(existingTask); // chép dữ liệu từ request vào existingTask (task lấy từ database)
        normalizeOptionalFields(existingTask); // lần 2 bảo đảm task thật được lưu xuống database cũng đã làm sạch
        normalizeUpdatedAt(existingTask, requestTask); // cập nhật thời gian theo requestTask nếu fe gửi hợp lệ, không thì lấy từ sv
        existingTask.setVersion(SyncMetadata.nextVersion(existingTask.getVersion()));
        existingTask.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(request.deviceId()));
        return TaskResponse.from(taskRepository.save(existingTask));
    }

    public TaskResponse softDeleteTask(String id, String deviceId, String userId) {
        Task task = findAccessibleTask(id, userId, "Task not found");
        updateDeletionMetadata(task, true, deviceId);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse restoreTask(String id, String deviceId, String userId) {
        Task task = taskRepository.findAccessibleById(id, requireUserId(userId))
                .filter(Task::isDeleted)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found in trash"
                ));

        updateDeletionMetadata(task, false, deviceId);
        return TaskResponse.from(taskRepository.save(task));
    }

    public void permanentlyDeleteTask(String id, String userId) {
        Task task = findAccessibleTask(id, userId, "Task not found in trash");
        if (!task.isDeleted()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Task must be in trash to be permanently deleted"
            );
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public void emptyTrash(String userId) {
        taskRepository.deleteAccessibleTrash(requireUserId(userId));
    }

    private Task findAccessibleTask(String id, String userId, String notFoundMessage) {
        return taskRepository.findAccessibleById(id, requireUserId(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage));
    }

    private void validateCategory(String categoryId, String userId) {
        // Task không có category thì bỏ qua kiểm tra
        if (categoryId == null || categoryId.isBlank()) {
            return;
        }
        // Chỉ duyệt category chưa bị xoá mềm, không thì báo lỗi
        categoryRepository.findAccessibleById(categoryId, userId)
                .filter(category -> !category.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
    }

    /**
     * Cập nhật data cho các task delete hoặc restore
     * @param task
     * @param deleted
     * @param deviceId
     */
    private static void updateDeletionMetadata(Task task, boolean deleted, String deviceId) {
        long now = System.currentTimeMillis();
        task.setDeleted(deleted);
        task.setDeletedAt(deleted ? now : null);
        task.setUpdatedAt(now);
        task.setVersion(SyncMetadata.nextVersion(task.getVersion()));
        task.setLastModifiedDeviceId(SyncMetadata.normalizeDeviceId(deviceId));
    }

    /**
     * Kiểm tra updatedAt, giá trị không hợp lý thì gán thời gian hiện tại
     * @param targetTask: task cần cập nhật
     * @param requestTask: task gửi lên
     * TODO: Giải quyết xung đột do người dùng chỉnh tgian ở thiết bị
     */
    private static void normalizeUpdatedAt(Task targetTask, Task requestTask) {
        targetTask.setUpdatedAt(requestTask.getUpdatedAt() > 0
                ? requestTask.getUpdatedAt()
                : System.currentTimeMillis());
    }

    /**
     * Chuẩn hóa, kiểm tra lại các trường không bắt buộc của task, rỗng thì đổi thành null
     * @param task
     */
    private static void normalizeOptionalFields(Task task) {
        task.setDescription(blankToNull(task.getDescription()));
        task.setCategoryId(blankToNull(task.getCategoryId()));
        task.setDeadline(blankToNull(task.getDeadline()));
        task.setPriority(blankToNull(task.getPriority()));
        task.setImagePath(blankToNull(task.getImagePath()));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static String requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userId;
    }
}
