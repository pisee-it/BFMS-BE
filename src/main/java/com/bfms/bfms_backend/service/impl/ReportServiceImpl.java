package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.res.RouteReportResponse;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.ReportRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.EconomyReportService;
import com.bfms.bfms_backend.service.ReportService;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final RouteRepository routeRepository;
    private final EconomyReportService economyReportService;

    @Override
    @Transactional
    public RouteReportResponse getRouteReport(Integer routeId, LocalDate startDate, LocalDate endDate) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));

        // Đảm bảo dữ liệu được đồng bộ trong khoảng thời gian yêu cầu (Tối ưu: Gọi bulk
        // sync cho routeId cụ thể)
        economyReportService.syncEconomyReports(routeId, startDate, endDate);

        // Lấy dữ liệu tổng hợp từ Repository
        List<Object[]> results = reportRepository.getSummaryByRouteAndDateRange(routeId, startDate, endDate);
        Object[] result = (results != null && !results.isEmpty()) ? results.get(0) : null;

        if (result == null || result.length == 0 || result[0] == null) {
            return new RouteReportResponse(
                    routeId,
                    route.getStopA() + " - " + route.getStopB(),
                    route.getRouteNumber(),
                    BigDecimal.ZERO, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO,
                    startDate, endDate);
        }

        // Mapping index từ Query trong ReportRepository:
        // 0: SUM(totalTicketRevenue)
        // 1: SUM(totalAdRevenue)
        // 2: SUM(totalPassengers)
        // 3: SUM(taxDeduction)
        // 4: SUM(netProfit)
        return new RouteReportResponse(
                routeId,
                route.getStopA() + " - " + route.getStopB(),
                route.getRouteNumber(),
                (BigDecimal) result[0],
                (BigDecimal) result[1],
                ((Long) result[2]).intValue(),
                (BigDecimal) result[3],
                (BigDecimal) result[4],
                startDate,
                endDate);
    }

    @Override
    public byte[] exportRouteReportToExcel(Integer routeId, LocalDate startDate, LocalDate endDate) {
        RouteReportResponse data = getRouteReport(routeId, startDate, endDate);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Báo cáo tuyến xe");

            // Tạo style cho Header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo style cho tiền tệ
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0\" VNĐ\""));

            // Tiêu đề báo cáo
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DOANH THU TUYẾN XE: " + data.routeName());

            // Thông tin tuyến
            Row infoRow1 = sheet.createRow(2);
            infoRow1.createCell(0).setCellValue("Số hiệu tuyến:");
            infoRow1.createCell(1).setCellValue(data.routeNumber());

            Row infoRow2 = sheet.createRow(3);
            infoRow2.createCell(0).setCellValue("Thời gian:");
            infoRow2.createCell(1).setCellValue(startDate.toString() + " đến " + endDate.toString());

            // Header bảng
            String[] headers = { "Hạng mục", "Giá trị" };
            Row headerRow = sheet.createRow(5);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dữ liệu chi tiết (đã được khấu trừ thuế & chi phí tại EconomyReport)
            int rowIdx = 6;
            addRow(sheet, rowIdx++, "Doanh thu vé (VAT 0%)", data.totalTicketRevenue(), currencyStyle);
            addRow(sheet, rowIdx++, "Doanh thu quảng cáo (Sau thuế 10%)", data.totalAdRevenue(), currencyStyle);
            addRow(sheet, rowIdx++, "Tổng lượt khách", data.totalPassengers(), null);
            addRow(sheet, rowIdx++, "Thuế khấu trừ (VAT + TNDN 20%)", data.taxDeduction(), currencyStyle);

            Row profitRow = sheet.createRow(rowIdx);
            profitRow.createCell(0).setCellValue("LỢI NHUẬN RÒNG");
            Cell profitCell = profitRow.createCell(1);
            profitCell.setCellValue(data.netProfit().doubleValue());
            profitCell.setCellStyle(currencyStyle);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private void addRow(Sheet sheet, int rowIdx, String label, Object value, CellStyle style) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        Cell valueCell = row.createCell(1);
        if (value instanceof BigDecimal bd) {
            valueCell.setCellValue(bd.doubleValue());
        } else if (value instanceof Integer i) {
            valueCell.setCellValue(i);
        }
        if (style != null) {
            valueCell.setCellStyle(style);
        }
    }
}
