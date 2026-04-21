package service;

import dtos.req.NodeRequest;
import entity.Node;

public interface NodeService {
    // 1. Tạo mới một Nốt chạy cho Tuyến xe
    Node createNode(Integer routeId, NodeRequest request);
}
