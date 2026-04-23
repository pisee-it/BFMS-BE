package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.NodeRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final RouteRepository routeRepository;

    @Override
    public Node createNode(Integer routeId, NodeRequest request) {
        // 1. Kiểm tra Tuyến xe có tồn tại không
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // 2. Map DTO sang Entity Node
        Node node = new Node();
        node.setRoute(route);
        node.setNodeNumber(request.nodeNumber());
        node.setExecutionDate(request.executionDate());
        node.setDescription(request.description());

        // 3. Lưu vào Database
        return nodeRepository.save(node);
    }
}
