package com.example.tisu.controller;

import java.util.List;

import com.example.tisu.dto.CategoryRequest;
import com.example.tisu.dto.CategoryResponse;
import com.example.tisu.service.CategoryService;
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
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getCategories(@AuthenticationPrincipal String userId) {
        return categoryService.getCategories(userId);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request, userId));
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal String userId) {
        return categoryService.updateCategory(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public CategoryResponse deleteCategory(
            @PathVariable String id,
            @RequestParam(required = false) String deviceId,
            @AuthenticationPrincipal String userId) {
        return categoryService.deleteCategory(id, deviceId, userId);
    }
}
