package entity;

public enum ShiftStatus {
    PLANNED,      // Ca chạy dự kiến
    IN_PROGRESS,  // Đang chạy (Chỉ trạng thái này mới được Hoàn thành)
    COMPLETED,    // Đã hoàn thành
    CANCELLED     // Đã hủy
}
