package com.example.tisu.controller;

import java.util.List;

import com.example.tisu.dto.HabitCompletionRequest;
import com.example.tisu.dto.HabitCompletionResponse;
import com.example.tisu.service.HabitCompletionService;
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
@RequestMapping("/api/habit-completions")
@CrossOrigin(origins = "*")
public class HabitCompletionController {
    private final HabitCompletionService habitCompletionService;

    public HabitCompletionController(HabitCompletionService habitCompletionService) {
        this.habitCompletionService = habitCompletionService;
    }

    @GetMapping
    public List<HabitCompletionResponse> getCompletions(@AuthenticationPrincipal String userId) {
        return habitCompletionService.getCompletions(userId);
    }

    @PostMapping
    public ResponseEntity<HabitCompletionResponse> createCompletion(
            @Valid @RequestBody HabitCompletionRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitCompletionService.createCompletion(request, userId));
    }

    @PutMapping("/{id}")
    public HabitCompletionResponse updateCompletion(
            @PathVariable String id,
            @Valid @RequestBody HabitCompletionRequest request,
            @AuthenticationPrincipal String userId) {
        return habitCompletionService.updateCompletion(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public HabitCompletionResponse deleteCompletion(
            @PathVariable String id,
            @RequestParam(required = false) String deviceId,
            @AuthenticationPrincipal String userId) {
        return habitCompletionService.deleteCompletion(id, deviceId, userId);
    }
}
