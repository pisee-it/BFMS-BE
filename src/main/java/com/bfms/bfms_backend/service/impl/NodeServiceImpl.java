package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.dtos.res.NodeResponse;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.mapper.NodeMapper;
import com.bfms.bfms_backend.repository.NodeRepository;
import com.bfms.bfms_backend.service.NodeService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final NodeMapper nodeMapper;
    private final com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper;

    @Override
    @Transactional
    public NodeResponse createNode(Integer routeId, NodeRequest request) {
        if (nodeRepository.existsByRouteIdAndExecutionDateAndNodeNumber(routeId, request.executionDate(),
                request.nodeNumber())) {
            throw new AppException(ErrorCode.NODE_ALREADY_EXISTS);
        }

        Route route = lookupHelper.getRoute(routeId);

        Node node = new Node();
        node.setRoute(route);
        node.setNodeNumber(request.nodeNumber());
        node.setExecutionDate(request.executionDate());
        node.setDescription(request.description());

        return nodeMapper.toResponse(nodeRepository.save(node));
    }

    @Override
    public List<NodeResponse> getNodesByRoute(Integer routeId) {
        // Kiểm tra route tồn tại
        lookupHelper.getRoute(routeId);

        return nodeRepository.findByRouteId(routeId).stream()
                .map(nodeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NodeResponse getNodeById(Integer id) {
        Node node = lookupHelper.getNode(id);
        return nodeMapper.toResponse(node);
    }
}

