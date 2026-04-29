package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.NodeRepository;
import com.bfms.bfms_backend.service.NodeService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper;

    @Override
    @Transactional
    public Node createNode(Integer routeId, NodeRequest request) {
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

        return nodeRepository.save(node);
    }
}
