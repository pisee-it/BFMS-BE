package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Quản lý Nốt (Nodes)", description = "Các API quản lý nốt chạy (lượt chạy) trong ngày của tuyến")
public class NodeController {

    private final NodeService nodeService;

    // 1. POST /routes/{routeId}/nodes - Tạo nốt chạy mới cho một tuyến
    @PostMapping("/routes/{routeId}/nodes")
    @Operation(summary = "Tạo nốt chạy mới", description = "Tạo một nốt chạy mới cho một tuyến xe buýt cụ thể trong một ngày.")
    public ResponseEntity<Node> createNode(@PathVariable Integer routeId, @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.createNode(routeId, request));
    }
}
