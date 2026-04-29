package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.BusShiftRequest;
import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.dtos.res.ShiftResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.mapper.BusShiftMapper;
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
    private final BusShiftMapper busShiftMapper;

    @Override
    public BusShift createBusShift(Integer nodeId, BusShiftRequest request) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found"));
        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));
        AppUser driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        BusShift shift = new BusShift();
        shift.setNode(node);
        shift.setBus(bus);
        shift.setDriver(driver);
        shift.setShiftOrder(request.shiftOrder());
        shift.setPlannedDepartureTime(request.plannedDepartureTime());
        shift.setPlannedArrivalTime(request.plannedArrivalTime());
        shift.setStatus(request.status());
        shift.setDirection(request.direction());

        return busShiftRepository.save(shift);
    }

    @Override
    public List<BusShiftResponse> getActiveShiftsByRoute(Integer routeId) {
        return busShiftRepository.findActiveShifts(routeId, "IN_PROGRESS")
                .stream()
                .map(busShiftMapper::toBusShiftResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShiftResponse completeShift(Integer shiftId, CompleteShiftRequest request) {
        BusShift shift = busShiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        if (!LocalDate.now().equals(shift.getNode().getExecutionDate())) {
            throw new RuntimeException("Cannot complete shift: Current date does not match Node's execution date.");
        }

        if (shift.getStatus() != ShiftStatus.IN_PROGRESS) {
            throw new RuntimeException("Only shifts with 'IN_PROGRESS' status can be completed.");
        }

        BigDecimal singlePrice = shift.getNode().getRoute().getPrice();
        BigDecimal revenue = singlePrice.multiply(BigDecimal.valueOf(request.total_single_tickets()));

        shift.setTotalSingleTickets(request.total_single_tickets());
        shift.setTotalMonthlyTickets(request.total_monthly_tickets());
        shift.setShiftRevenue(revenue);
        shift.setStatus(ShiftStatus.COMPLETED);

        busShiftRepository.saveAndFlush(shift);

        saveTicketRecord(shift, "SINGLE", request.total_single_tickets());
        saveTicketRecord(shift, "MONTHLY", request.total_monthly_tickets());

        Node node = shift.getNode();
        Integer totalPassengers = busShiftRepository.sumPassengersByNodeId(node.getId(), ShiftStatus.COMPLETED);
        node.setTotalPassengers(totalPassengers);
        nodeRepository.save(node);

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

        return busShiftMapper.toShiftResponse(busShiftRepository.save(shift));
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
}
