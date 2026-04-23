package com.bfms.bfms_backend.entity;

public enum BusStatus {
    ACTIVE,      // Đang hoạt động
    INACTIVE,    // Tạm ngưng (nghỉ ca)
    MAINTENANCE, // Đang bảo trì/sửa chữa
    SOLD         // Đã bán (giữ lịch sử, không cho vận hành mới)
}
