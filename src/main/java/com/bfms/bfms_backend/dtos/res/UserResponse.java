package com.bfms.bfms_backend.dtos.res;

import com.bfms.bfms_backend.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Thông tin phản hồi người dùng")
public record UserResponse(
        @Schema(description = "ID người dùng", example = "1")
        Integer id,

        @Schema(description = "Tên đăng nhập", example = "admin")
        String username,

        @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
        String fullName,

        @Schema(description = "Tuổi", example = "30")
        Integer age,

        @Schema(description = "Loại bằng lái", example = "E")
        String licenceType,

        @Schema(description = "Link ảnh đại diện", example = "http://example.com/avatar.png")
        String avatarUrl,

        @Schema(description = "Vai trò người dùng")
        Role role
) {
}
