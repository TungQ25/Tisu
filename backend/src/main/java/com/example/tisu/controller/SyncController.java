package com.example.tisu.controller;

import com.example.tisu.dto.SyncOperationRequest;
import com.example.tisu.dto.SyncOperationResponse;
import com.example.tisu.service.SyncService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
public class SyncController {
    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/operations")
    public List<SyncOperationResponse> getOperations(
            @RequestParam(defaultValue = "0") long since,
            @AuthenticationPrincipal String userId) {
        return syncService.getOperations(since, userId);
    }

    @PostMapping("/operations")
    public ResponseEntity<SyncOperationResponse> createOperation(
            @Valid @RequestBody SyncOperationRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(syncService.createOperation(request, userId));
    }
}
