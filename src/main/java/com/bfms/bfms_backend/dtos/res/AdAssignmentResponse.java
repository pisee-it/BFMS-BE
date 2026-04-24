package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.AdAssignmentStatus;

public record AdAssignmentResponse(
        Integer id,
        Integer adContractId,
        Integer busId,
        String licensePlate,
        AdAssignmentStatus status,
        Boolean needsAttention
) {}
