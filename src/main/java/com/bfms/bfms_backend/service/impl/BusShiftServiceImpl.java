package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.BusShiftRequest;
import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.*;
import jakarta.transaction.Transactional;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.service.BusShiftService;
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
    private final DailyTicketStatRepository dailyTicketStatRepository;
    private final TicketRepository ticketRepository;

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
        shift.setDirection(request.direction());

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
                        shift.getStatus(),
                        shift.getDirection()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShiftResponse completeShift(Integer shiftId, CompleteShiftRequest request) {
        // 1. Tìm ca chạy
        BusShift shift = busShiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        // 2. VALIDATION: Kiểm tra ngày thực hiện (Chỉ được hoàn thành trong đúng ngày
        // nốt chạy)
        if (!LocalDate.now().equals(shift.getNode().getExecutionDate())) {
            throw new RuntimeException("Cannot complete shift: Current date does not match Node's execution date.");
        }

        // 3. VALIDATION: Kiểm tra luồng trạng thái
        if (shift.getStatus() != ShiftStatus.IN_PROGRESS) {
            throw new RuntimeException("Only shifts with 'IN_PROGRESS' status can be completed.");
        }

        // 4. Tính toán doanh thu (Chỉ tính doanh thu dựa trên vé lượt - Single Ticket)
        BigDecimal singlePrice = shift.getNode().getRoute().getPrice();
        BigDecimal revenue = singlePrice.multiply(BigDecimal.valueOf(request.total_single_tickets()));

        // 5. Cập nhật dữ liệu Ca chạy
        shift.setTotalSingleTickets(request.total_single_tickets());
        shift.setTotalMonthlyTickets(request.total_monthly_tickets());
        shift.setShiftRevenue(revenue);
        shift.setStatus(ShiftStatus.COMPLETED);

        busShiftRepository.saveAndFlush(shift);

        // 6. Lưu vết vào bảng TICKET (Audit)
        saveTicketRecord(shift, "SINGLE", request.total_single_tickets());
        saveTicketRecord(shift, "MONTHLY", request.total_monthly_tickets());

        // 7. Cập nhật Node (Derived Field: total_passengers)
        Node node = shift.getNode();
        Integer totalPassengers = busShiftRepository.sumPassengersByNodeId(node.getId(), ShiftStatus.COMPLETED);
        node.setTotalPassengers(totalPassengers);
        nodeRepository.save(node);

        // 8. Cập nhật Daily Ticket Stat
        LocalDate reportDate = node.getExecutionDate();
        Integer routeId = node.getRoute().getId();
        DailyTicketStat stat = dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, reportDate)
                .orElseGet(() -> {
                    DailyTicketStat newStat = new DailyTicketStat();
                    newStat.setRoute(node.getRoute());
                    newStat.setReportDate(reportDate);
                    return newStat;
                });
        stat.addTickets(request.total_single_tickets(), request.total_monthly_tickets());
        dailyTicketStatRepository.save(stat);

        return mapToResponse(busShiftRepository.save(shift));
    }

    private void saveTicketRecord(BusShift shift, String type, Integer quantity) {
        if (quantity != null && quantity > 0) {
            Ticket ticket = new Ticket();
            ticket.setBusShift(shift);
            ticket.setType(type);
            ticket.setQuantity(quantity);
            ticketRepository.save(ticket);
        }
    }

    private ShiftResponse mapToResponse(BusShift shift) {
        return new ShiftResponse(
                shift.getId(),
                shift.getBus().getLicensePlate(),
                shift.getDriver().getFullName(),
                shift.getShiftOrder(),
                shift.getStatus(),
                shift.getShiftRevenue(),
                shift.getPlannedDepartureTime(),
                shift.getDirection());
    }
}
