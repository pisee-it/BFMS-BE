package com.bfms.bfms_backend.dtos.req;

import com.bfms.bfms_backend.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Yêu cầu tạo hoặc cập nhật người dùng")
public record UserRequest(
        @Schema(description = "Tên đăng nhập", example = "admin")
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
        String username,

        @Schema(description = "Mật khẩu", example = "123456")
        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
        String password,

        @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @Schema(description = "Tuổi", example = "30")
        @Min(value = 1, message = "Tuổi phải lớn hơn 0")
        Integer age,

        @Schema(description = "Loại bằng lái (nếu có)", example = "E")
        String licenceType,

        @Schema(description = "Link ảnh đại diện", example = "http://example.com/avatar.png")
        String avatarUrl,

        @Schema(description = "Vai trò người dùng")
        @NotNull(message = "Vai trò không được để trống")
        Role role
) {
}
