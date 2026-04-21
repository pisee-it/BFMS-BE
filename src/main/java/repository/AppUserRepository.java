package repository;

import entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Tìm kiếm người dùng theo username để phục vụ quá trình xác thực (Login/JWT)
    // Optional tránh lỗi NullPointerException khi không tìm thấy user
    Optional<AppUser> findByUsername(String username);
}
