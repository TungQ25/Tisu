package com.example.tisu.controller;

import java.util.List;

import com.example.tisu.dto.HabitRequest;
import com.example.tisu.dto.HabitResponse;
import com.example.tisu.service.HabitService;
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
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {
    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public List<HabitResponse> getHabits(@AuthenticationPrincipal String userId) {
        return habitService.getHabits(userId);
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
            @Valid @RequestBody HabitRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitService.createHabit(request, userId));
    }

    @PutMapping("/{id}")
    public HabitResponse updateHabit(
            @PathVariable String id,
            @Valid @RequestBody HabitRequest request,
            @AuthenticationPrincipal String userId) {
        return habitService.updateHabit(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public HabitResponse deleteHabit(
            @PathVariable String id,
            @RequestParam(required = false) String deviceId,
            @AuthenticationPrincipal String userId) {
        return habitService.deleteHabit(id, deviceId, userId);
    }
}
