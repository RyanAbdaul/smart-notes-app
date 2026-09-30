package com.smartnotes.app.backend.rest;

import com.smartnotes.app.backend.request.UpdateUserRequest;
import com.smartnotes.app.backend.response.UserResponse;
import com.smartnotes.app.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin API")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all registered users (Admin only)"
    )
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update user details",
            description = "Partially updates user details and info (firstName, lastName, email) (Admin only)"
    )
    public UserResponse updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        return adminService.updateUser(id, request);
    }

    @PatchMapping("/users/{id}/promote")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Promote user to admin",
            description = "Promotes an existing user by granting the ROLE_ADMIN role (Admin only)"
    )
    public UserResponse promoteToAdmin(@PathVariable UUID id) {
        return adminService.promoteToAdmin(id);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a user",
            description = "Deletes a user by ID. Admins cannot be deleted. (Admin only)"
    )
    public void deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
    }
}
