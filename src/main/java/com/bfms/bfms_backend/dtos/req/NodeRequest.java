package com.bfms.bfms_backend.dtos.req;

import java.time.LocalDate;

public record NodeRequest(
                Integer nodeNumber,
                LocalDate executionDate,
                String description) {
}
