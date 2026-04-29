package com.bfms.bfms_backend.mapper;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;
import com.bfms.bfms_backend.entity.AdAssignment;
import com.bfms.bfms_backend.entity.AdAssignmentStatus;
import com.bfms.bfms_backend.entity.AdCompany;
import com.bfms.bfms_backend.entity.AdContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface AdMapper {

    // --- Company ---
    AdCompanyResponse toCompanyResponse(AdCompany company);

    @Mapping(target = "id", ignore = true)
    AdCompany toCompanyEntity(AdCompanyRequest request);

    // --- Contract ---
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "route.id", target = "routeId")
    @Mapping(source = "route.routeNumber", target = "routeNumber")
    AdContractResponse toContractResponse(AdContract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "route", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AdContract toContractEntity(AdContractRequest request);

    // --- Assignment ---
    @Mapping(source = "adContract.id", target = "adContractId")
    @Mapping(source = "bus.id", target = "busId")
    @Mapping(source = "bus.licensePlate", target = "licensePlate")
    @Mapping(target = "needsAttention", source = "assignment", qualifiedByName = "calculateNeedsAttention")
    AdAssignmentResponse toAssignmentResponse(AdAssignment assignment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adContract", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "status", ignore = true)
    AdAssignment toAssignmentEntity(AdAssignmentRequest request);

    @Named("calculateNeedsAttention")
    default boolean calculateNeedsAttention(AdAssignment assignment) {
        if (assignment == null || assignment.getAdContract() == null) return false;
        return assignment.getAdContract().getEndDate().isBefore(LocalDate.now())
                && assignment.getStatus() == AdAssignmentStatus.ACTIVE;
    }
}
