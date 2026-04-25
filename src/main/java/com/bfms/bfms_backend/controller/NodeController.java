package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.service.NodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController {

    private final NodeService nodeService;

    // 1. POST /routes/{routeId}/nodes - Tạo nốt chạy mới cho một tuyến
    @PostMapping("/routes/{routeId}/nodes")
    public ResponseEntity<Node> createNode(@PathVariable Integer routeId, @Valid @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.createNode(routeId, request));
    }
}
