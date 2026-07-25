package com.example.tisu.controller;

import java.util.List;

import com.example.tisu.dto.TaskRequest;
import com.example.tisu.dto.TaskResponse;
import com.example.tisu.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(@AuthenticationPrincipal String userId) {
        return taskService.getAllTasks(userId);
    }

    @GetMapping("/trash")
    public List<TaskResponse> getTrash(@AuthenticationPrincipal String userId) {
        return taskService.getTrash(userId);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable String id, @AuthenticationPrincipal String userId) {
        return taskService.getTaskById(id, userId);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, userId));
    }

    // Ko dùng ResponseEntity vì mặc định trả về status 200 OK nếu thành công
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal String userId) {
        return taskService.updateTask(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public TaskResponse softDeleteTask(
            @PathVariable String id,
            @RequestParam(required = false) String deviceId,
            @AuthenticationPrincipal String userId) {
        return taskService.softDeleteTask(id, deviceId, userId);
    }

    @PostMapping("/{id}/restore")
    public TaskResponse restoreTask(
            @PathVariable String id,
            @RequestParam(required = false) String deviceId,
            @AuthenticationPrincipal String userId) {
        return taskService.restoreTask(id, deviceId, userId);
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentlyDeleteTask(
            @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        taskService.permanentlyDeleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/trash")
    public ResponseEntity<Void> emptyTrash(@AuthenticationPrincipal String userId) {
        taskService.emptyTrash(userId);
        return ResponseEntity.noContent().build();
    }
}
