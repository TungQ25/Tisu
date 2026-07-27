package com.example.tisu.controller;

import com.example.tisu.dto.CategoryRequest;
import com.example.tisu.dto.CategoryResponse;
import com.example.tisu.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Test
    void createCategoryDelegatesToServiceAndReturnsCreated() {
        CategoryRequest request = mock(CategoryRequest.class);
        CategoryResponse response = mock(CategoryResponse.class);
        when(categoryService.createCategory(request, "user-1")).thenReturn(response);
        CategoryController controller = new CategoryController(categoryService);

        ResponseEntity<CategoryResponse> result = controller.createCategory(request, "user-1");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(categoryService).createCategory(request, "user-1");
    }
}
