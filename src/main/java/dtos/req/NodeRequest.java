package dtos.req;

import java.time.LocalDate;

public record NodeRequest(
        Integer nodeNumber,
        LocalDate executionDate,
        Integer direction, // 1: A->B, 2: B->A
        String description
) {}
