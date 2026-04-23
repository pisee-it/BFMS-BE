package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    // Tìm kiếm người dùng theo username để phục vụ quá trình xác thực (Login/JWT)
    // Optional tránh lỗi NullPointerException khi không tìm thấy user
    Optional<AppUser> findByUsername(String username);
}
