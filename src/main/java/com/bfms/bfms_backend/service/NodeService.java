package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.dtos.res.NodeResponse;
import java.util.List;

public interface NodeService {
    // 1. Tạo mới một Nốt chạy cho Tuyến xe
    NodeResponse createNode(Integer routeId, NodeRequest request);

    // 2. Lấy danh sách nốt xe theo tuyến
    List<NodeResponse> getNodesByRoute(Integer routeId);

    // 3. Lấy chi tiết nốt xe
    NodeResponse getNodeById(Integer id);

    // 4. Cập nhật nốt xe
    NodeResponse updateNode(Integer id, NodeRequest request);

    // 5. Xóa nốt xe
    void deleteNode(Integer id);
}

