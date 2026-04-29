package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.RouteRequest;
import com.bfms.bfms_backend.dtos.res.RouteResponse;
import com.bfms.bfms_backend.entity.Route;
import jakarta.transaction.Transactional;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.RouteService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<RouteResponse> getAllRoutes() {
        // Lấy toàn bộ danh sách từ DB và chuyển đổi sang DTO
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponse getRouteById(Integer id) {
        // Tìm kiếm theo ID, ném ngoại lệ nếu không tồn tại
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));
        return mapToResponse(route);
    }

    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        // 1. Kiểm tra validation cơ bản (không âm)
        validateRouteDistances(request.distanceAB(), request.distanceBA());

        Route route = new Route();
        mapRequestToEntity(route, request);

        // 2. Tự động tính toán và cập nhật giá vé trước khi lưu
        BigDecimal autoPrice = calculateAutomaticPrice(request.distanceAB(), request.distanceBA());
        route.setPrice(autoPrice);

        return mapToResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Integer id, RouteRequest request) {
        // Kiểm tra sự tồn tại của bản ghi trước khi cập nhật
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));

        // Cập nhật các trường dữ liệu mới
        mapRequestToEntity(route, request);
        return mapToResponse(routeRepository.save(route));
    }

    @Override
    @Transactional
    public void deleteRoute(Integer id) {
        // Xóa cứng bản ghi (Lưu ý: Thực tế nên cân nhắc kiểm tra ràng buộc FK với bảng BUS/NODE)
        if (!routeRepository.existsById(id)) {
            throw new AppException(ErrorCode.ROUTE_NOT_FOUND);
        }

        // 2. Kiểm tra xem có Xe (Bus) nào đang thuộc tuyến này không
//        if (busRepository.existsByRouteId(id)) {
//            throw new RuntimeException("Không thể xóa: Hiện có xe đang được phân bổ cho tuyến này");
//        }

        // 3. Kiểm tra xem có Nốt (Node) nào đang thuộc tuyến này không
//        if (nodeRepository.existsByRouteId(id)) {
//            throw new RuntimeException("Không thể xóa: Tuyến đã được chia lịch chạy (Node)");
//        }
        routeRepository.deleteById(id);
    }

    // --- Logic tính giá vé tự động ---
    private BigDecimal calculateAutomaticPrice(BigDecimal distAB, BigDecimal distBA) {
        // Tính trung bình cộng: (A + B) / 2
        BigDecimal average = distAB.add(distBA)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        double avg = average.doubleValue();

        // Áp dụng quy tắc phân bậc giá vé
        if (avg < 15) return BigDecimal.valueOf(8000);
        if (avg < 25) return BigDecimal.valueOf(10000);
        if (avg < 30) return BigDecimal.valueOf(12000);
        if (avg < 40) return BigDecimal.valueOf(15000);
        return BigDecimal.valueOf(20000);
    }

    private void validateRouteDistances(BigDecimal ab, BigDecimal ba) {
        if (ab.compareTo(BigDecimal.ZERO) < 0 || ba.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.INVALID_ROUTE_DISTANCE);
        }
    }

    // --- Helper Methods ---
    private void mapRequestToEntity(Route route, RouteRequest request) {
        route.setRouteNumber(request.routeNumber());
        route.setStopA(request.stopA());
        route.setStopB(request.stopB());
        route.setPath(request.path());
        route.setDistanceAB(request.distanceAB());
        route.setDistanceBA(request.distanceBA());
        route.setOperationStart(request.operationStart());
        route.setOperationEnd(request.operationEnd());
        route.setPrice(request.price());
    }

    private RouteResponse mapToResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getRouteNumber(),
                route.getStopA(),
                route.getStopB(),
                route.getPath(),
                route.getDistanceAB(),
                route.getDistanceBA(),
                route.getOperationStart(),
                route.getOperationEnd(),
                route.getPrice()
        );
    }
}
