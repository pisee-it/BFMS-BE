package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;
import com.bfms.bfms_backend.service.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý Module Quảng cáo.
 * Phân quyền dựa trên vai trò được định nghĩa trong PROJECT_CONTEXT.md.
 */
@RestController
@RequestMapping("/api/v1/ads")
@Tag(name = "Quảng cáo (Advertising)", description = "Các API quản lý đối tác, hợp đồng quảng cáo và phân bổ decal lên xe")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    // --- Quản lý Công ty Quảng cáo ---

    @PostMapping("/companies")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'ADMIN', 'OWNER')")
    @Operation(summary = "Tạo công ty quảng cáo", description = "Đăng ký một đối tác quảng cáo mới vào hệ thống.")
    public ResponseEntity<AdCompanyResponse> createCompany(@Valid @RequestBody AdCompanyRequest request) {
        return ResponseEntity.ok(adService.createCompany(request));
    }

    @GetMapping("/companies")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'ADMIN', 'ACCOUNTANT')")
    @Operation(summary = "Lấy danh sách công ty", description = "Trả về toàn bộ danh sách các đối tác quảng cáo. Quyền: ADVERTISING, ADMIN, ACCOUNTANT")
    public ResponseEntity<List<AdCompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(adService.getAllCompanies());
    }

    // --- Quản lý Hợp đồng Quảng cáo ---

    // US-04: Tạo yêu cầu hợp đồng
    @PostMapping("/contracts")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'OWNER')")
    @Operation(summary = "Tạo hợp đồng quảng cáo", description = "Tạo một yêu cầu hợp đồng quảng cáo mới. Trạng thái mặc định là PENDING. Quyền: ADVERTISING, OWNER")
    public ResponseEntity<AdContractResponse> createContract(@Valid @RequestBody AdContractRequest request) {
        return ResponseEntity.ok(adService.createContract(request));
    }

    @GetMapping("/contracts")
    @PreAuthorize("hasAnyRole('ADVERTISING', 'ADMIN', 'ACCOUNTANT', 'OWNER')")
    @Operation(summary = "Lấy danh sách hợp đồng", description = "Trả về danh sách các hợp đồng quảng cáo. Quyền: ADVERTISING, ADMIN, ACCOUNTANT, OWNER")
    public ResponseEntity<List<AdContractResponse>> getAllContracts() {
        return ResponseEntity.ok(adService.getAllContracts());
    }

    // US-06: Phê duyệt hợp đồng
    @PatchMapping("/contracts/{id}/approve")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    @Operation(summary = "Phê duyệt hợp đồng", description = "Kế toán xác nhận thanh toán và kích hoạt hiệu lực hợp đồng. Quyền: ACCOUNTANT")
    public ResponseEntity<AdContractResponse> approveContract(@PathVariable Integer id) {
        return ResponseEntity.ok(adService.approveContract(id));
    }

    // Yêu cầu xóa hợp đồng (Accountant yêu cầu, Owner thực thi)
    @PatchMapping("/contracts/{id}/request-delete")
    @PreAuthorize("hasRole('ACCOUNTANT')")
    @Operation(summary = "Yêu cầu xóa hợp đồng", description = "Kế toán gửi yêu cầu xóa hợp đồng cho Chủ doanh nghiệp. Quyền: ACCOUNTANT")
    public ResponseEntity<AdContractResponse> requestDeleteContract(@PathVariable Integer id) {
        return ResponseEntity.ok(adService.requestDeleteContract(id));
    }

    @DeleteMapping("/contracts/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Xóa hợp đồng", description = "Chủ doanh nghiệp thực hiện xóa hợp đồng khỏi hệ thống. Quyền: OWNER")
    public ResponseEntity<Void> deleteContract(@PathVariable Integer id) {
        adService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gán Quảng cáo lên Xe ---

    // US-05: Phân bổ quảng cáo lên xe
    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(summary = "Phân bổ quảng cáo lên xe", description = "Gán quảng cáo từ hợp đồng có hiệu lực lên các xe buýt cụ thể. Quyền: ADMIN, OWNER")
    public ResponseEntity<AdAssignmentResponse> assignAdToBus(@Valid @RequestBody AdAssignmentRequest request) {
        return ResponseEntity.ok(adService.assignAdToBus(request));
    }
}
