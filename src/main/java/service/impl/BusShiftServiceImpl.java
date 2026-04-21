package service.impl;

import dtos.req.BusShiftRequest;
import dtos.req.CompleteShiftRequest;
import dtos.res.BusShiftResponse;
import dtos.res.ShiftResponse;
import entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import repository.*;
import service.BusShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusShiftServiceImpl implements BusShiftService {

    private final BusShiftRepository busShiftRepository;
    private final NodeRepository nodeRepository;
    private final BusRepository busRepository;
    private final AppUserRepository userRepository;
    // Lấy giá vé tháng từ file cấu hình
    @Value("${bfms.pricing.monthly-ticket}")
    private BigDecimal monthlyTicketPrice;

    @Override
    public BusShift createBusShift(Integer nodeId, BusShiftRequest request) {
        // 1. Kiểm tra sự tồn tại của các thực thể liên quan (Node, Bus, Driver)
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found"));
        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));
        AppUser driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 2. Map dữ liệu sang Entity BusShift
        BusShift shift = new BusShift();
        shift.setNode(node);
        shift.setBus(bus);
        shift.setDriver(driver);
        shift.setShiftOrder(request.shiftOrder());
        shift.setPlannedDepartureTime(request.plannedDepartureTime());
        shift.setPlannedArrivalTime(request.plannedArrivalTime());
        shift.setStatus(request.status());

        // 3. Lưu ca chạy mới
        return busShiftRepository.save(shift);
    }

    @Override
    public List<BusShiftResponse> getActiveShiftsByRoute(Integer routeId) {
        // 1. Lấy danh sách từ DB với status cố định là IN_PROGRESS
        return busShiftRepository.findActiveShifts(routeId, "IN_PROGRESS")
                .stream()
                .map(shift -> new BusShiftResponse(
                        shift.getId(),
                        shift.getBus().getLicensePlate(),
                        shift.getDriver().getFullName(),
                        shift.getShiftOrder(),
                        shift.getPlannedDepartureTime(),
                        shift.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShiftResponse completeShift(Integer shiftId, CompleteShiftRequest request) {
        // 1. Tìm ca chạy
        BusShift shift = busShiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        // 2. VALIDATION: Kiểm tra ngày thực hiện (Chỉ được hoàn thành trong đúng ngày nốt chạy)
        if (!LocalDate.now().equals(shift.getNode().getExecutionDate())) {
            throw new RuntimeException("Cannot complete shift: Current date does not match Node's execution date.");
        }

        // 3. VALIDATION: Kiểm tra luồng trạng thái
        if (shift.getStatus() != ShiftStatus.IN_PROGRESS) {
            throw new RuntimeException("Only shifts with 'IN_PROGRESS' status can be completed.");
        }

        // 4. Tính toán doanh thu (Dùng singlePrice từ Route và monthlyPrice từ Config)
        BigDecimal singlePrice = shift.getNode().getRoute().getPrice();
        BigDecimal revenue = singlePrice.multiply(BigDecimal.valueOf(request.total_single_tickets()))
                .add(monthlyTicketPrice.multiply(BigDecimal.valueOf(request.total_monthly_tickets())));

        // 5. Cập nhật dữ liệu
        shift.setTotalSingleTickets(request.total_single_tickets());
        shift.setTotalMonthlyTickets(request.total_monthly_tickets());
        shift.setShiftRevenue(revenue);
        shift.setStatus(ShiftStatus.COMPLETED); // Chuyển trạng thái sang COMPLETED

        // 6. Cộng dồn hành khách vào Node
        Node node = shift.getNode();
        int newPassengers = request.total_single_tickets() + request.total_monthly_tickets();
        node.setTotalPassengers((node.getTotalPassengers() == null ? 0 : node.getTotalPassengers()) + newPassengers);

        nodeRepository.save(node);
        return mapToResponse(busShiftRepository.save(shift));
    }

    private ShiftResponse mapToResponse(BusShift shift) {
        return new ShiftResponse(
                shift.getId(),
                shift.getBus().getLicensePlate(),
                shift.getDriver().getFullName(),
                shift.getShiftOrder(),
                shift.getStatus(),
                shift.getShiftRevenue(),
                shift.getPlannedDepartureTime()
        );
    }
}
