package service.impl;

import dtos.req.NodeRequest;
import entity.Node;
import entity.Route;
import repository.NodeRepository;
import repository.RouteRepository;
import service.NodeService;
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
        node.setDirection(request.direction());
        node.setDescription(request.description());

        // 3. Lưu vào Database
        return nodeRepository.save(node);
    }
}
