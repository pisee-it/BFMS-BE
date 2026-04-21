package service.impl;

import dtos.req.BusRequest;
import dtos.res.BusResponse;
import entity.Bus;
import entity.BusStatus;
import entity.Route;
import jakarta.transaction.Transactional;
import repository.BusRepository;
import repository.RouteRepository;
import service.BusService;

import java.util.List;
import java.util.stream.Collectors;

public class BusServiceImpl implements BusService {
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public BusServiceImpl(BusRepository busRepository, RouteRepository routeRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public List<BusResponse> getAllBuses() {
        // Truy vấn toàn bộ xe và chuyển đổi sang Response DTO
        return busRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BusResponse createBus(BusRequest request) {
        // Tìm kiếm Route để gán cho Bus mới
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến xe ID: " + request.routeId()));

        // Khởi tạo và lưu thực thể Bus
        Bus bus = new Bus();
        mapRequestToEntity(bus, request, route);
        return mapToResponse(busRepository.save(bus));
    }

    @Override
    @Transactional
    public BusResponse updateBus(Integer id, BusRequest request) {
        // Kiểm tra xe có tồn tại trong hệ thống hay không
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe ID: " + id));

        // Cập nhật Route mới nếu có thay đổi routeId
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RuntimeException("Tuyến xe mới không hợp lệ"));

        mapRequestToEntity(bus, request, route);
        return mapToResponse(busRepository.save(bus));
    }

    @Override
    @Transactional
    public void deleteBus(Integer id) {
        // Xóa cứng thông tin xe (Lưu ý: Sẽ lỗi nếu đã có dữ liệu trong BUS_SHIFT)
        if (!busRepository.existsById(id)) {
            throw new RuntimeException("Xe không tồn tại");
        }

        // KIỂM TRA RÀNG BUỘC: Nếu xe đã có dữ liệu trong bảng BUS_SHIFT
        // Chúng ta KHÔNG ĐƯỢC xóa để đảm bảo tính toàn vẹn của lịch sử tài chính.
//        if (busShiftRepository.existsByBusId(id)) {
//            throw new RuntimeException("Không thể xóa: Xe này đã có dữ liệu vận hành (chuyến xe). " +
//                    "Vui lòng chỉ chuyển trạng thái sang INACTIVE thay vì xóa.");
//        }
        busRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void sellBus(Integer id) {
        // Tìm xe
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe để thực hiện giao dịch bán."));

        // 2. Kiểm tra xem xe có đang trong ca chạy (BUS_SHIFT) nào chưa hoàn thành không
        // (Lưu ý: Bạn nên viết thêm hàm kiểm tra status của SHIFT là 'PENDING' hoặc 'RUNNING')

        // 3. Cập nhật trạng thái sang SOLD
        // Trạng thái này giúp giữ lại lịch sử nhưng ngăn chặn việc đưa xe vào các lịch trình tương lai
        bus.setStatus(BusStatus.SOLD);

        // 4. Lưu lại thay đổi
        busRepository.save(bus);
    }

    // --- Helper Methods ---
    private void mapRequestToEntity(Bus bus, BusRequest request, Route route) {
        bus.setRoute(route);
        bus.setBusModel(request.busModel());
        bus.setManufacturer(request.manufacturer());
        bus.setCapacity(request.capacity());
        bus.setYom(request.yom());
        bus.setLicensePlate(request.licensePlate());
        bus.setStatus(request.status());
        bus.setIsAdvertised(request.isAdvertised());
    }

    private BusResponse mapToResponse(Bus bus) {
        return new BusResponse(
                bus.getId(),
                bus.getRoute().getId(),
                bus.getRoute().getRouteNumber(),
                bus.getBusModel(),
                bus.getManufacturer(),
                bus.getCapacity(),
                bus.getYom(),
                bus.getLicensePlate(),
                bus.getStatus(),
                bus.getIsAdvertised()
        );
    }
}
