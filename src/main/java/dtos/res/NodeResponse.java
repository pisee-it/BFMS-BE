package dtos.res;

import java.time.LocalDate;
import java.util.List;

public record NodeResponse(
        Integer id,
        String routeName,
        Integer nodeNumber,
        LocalDate executionDate,
        Integer totalPassengers,
        List<ShiftResponse> shifts // Trả về danh sách các ca chạy thuộc nốt này
) {}
