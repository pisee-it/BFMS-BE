package controller;

import dtos.req.NodeRequest;
import entity.Node;
import service.NodeService;
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
    public ResponseEntity<Node> createNode(@PathVariable Integer routeId, @RequestBody NodeRequest request) {
        return ResponseEntity.ok(nodeService.createNode(routeId, request));
    }
}
