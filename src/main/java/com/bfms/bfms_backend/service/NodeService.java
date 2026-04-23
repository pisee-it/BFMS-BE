package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.entity.Node;

public interface NodeService {
    // 1. Tạo mới một Nốt chạy cho Tuyến xe
    Node createNode(Integer routeId, NodeRequest request);
}
