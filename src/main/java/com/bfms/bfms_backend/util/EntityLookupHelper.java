package com.bfms.bfms_backend.util;

import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityLookupHelper {

    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final AppUserRepository appUserRepository;
    private final NodeRepository nodeRepository;
    private final BusShiftRepository busShiftRepository;
    private final AdCompanyRepository adCompanyRepository;
    private final AdContractRepository adContractRepository;

    public Route getRoute(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));
    }

    public Bus getBus(Integer id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUS_NOT_FOUND));
    }

    public AppUser getUser(Integer id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public Node getNode(Integer id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NODE_NOT_FOUND));
    }

    public BusShift getBusShift(Integer id) {
        return busShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));
    }

    public AdCompany getAdCompany(Integer id) {
        return adCompanyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AD_COMPANY_NOT_FOUND));
    }

    public AdContract getAdContract(Integer id) {
        return adContractRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AD_CONTRACT_NOT_FOUND));
    }
}
