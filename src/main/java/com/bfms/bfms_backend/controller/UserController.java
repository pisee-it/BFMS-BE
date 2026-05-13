package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.UserRequest;
import com.bfms.bfms_backend.dtos.res.UserResponse;
import com.bfms.bfms_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Quản lý Người dùng (Users)", description = "Các API quản lý tài khoản và nhân sự")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Danh sách người dùng", description = "Lấy toàn bộ danh sách người dùng. Quyền: ADMIN")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chi tiết người dùng", description = "Lấy thông tin chi tiết một người dùng theo ID. Quyền: ADMIN")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo người dùng mới", description = "Admin tạo tài khoản mới cho nhân sự. Quyền: ADMIN")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin tài khoản người dùng. Quyền: ADMIN")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa người dùng", description = "Xóa tài khoản người dùng khỏi hệ thống. Quyền: ADMIN")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Thông tin cá nhân", description = "Lấy thông tin người dùng đang đăng nhập. Quyền: Authenticated")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
