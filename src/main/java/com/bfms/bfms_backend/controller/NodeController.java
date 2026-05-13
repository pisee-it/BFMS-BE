package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.dtos.res.NodeResponse;
import com.bfms.bfms_backend.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Quản lý Nốt (Nodes)", description = "Các API quản lý nốt chạy (lượt chạy) trong ngày của tuyến")
public class NodeController {

    private final NodeService nodeService;

    // 1. POST /routes/{routeId}/nodes - Tạo nốt chạy mới cho một tuyến
    @PostMapping("/routes/{routeId}/nodes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo nốt chạy mới", description = "Tạo một nốt chạy mới cho một tuyến xe buýt cụ thể trong một ngày. Quyền: ADMIN")
    public ResponseEntity<NodeResponse> createNode(@PathVariable Integer routeId, @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.createNode(routeId, request));
    }

    // 2. GET /routes/{routeId}/nodes - Lấy danh sách nốt của một tuyến
    @GetMapping("/routes/{routeId}/nodes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách nốt theo tuyến", description = "Trả về danh sách các nốt xe (lượt chạy) của một tuyến cụ thể. Quyền: ADMIN")
    public ResponseEntity<List<NodeResponse>> getNodesByRoute(@PathVariable Integer routeId) {
        return ResponseEntity.ok(nodeService.getNodesByRoute(routeId));
    }

    // 4. PUT /nodes/{id} - Cập nhật một nốt xe
    @PutMapping("/nodes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật nốt xe", description = "Cập nhật thông tin nốt xe. Quyền: ADMIN")
    public ResponseEntity<NodeResponse> updateNode(@PathVariable Integer id, @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.updateNode(id, request));
    }

    // 5. DELETE /nodes/{id} - Xóa một nốt xe
    @DeleteMapping("/nodes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa nốt xe", description = "Xóa một nốt xe khỏi hệ thống. Quyền: ADMIN")
    public ResponseEntity<Void> deleteNode(@PathVariable Integer id) {
        nodeService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }
}

