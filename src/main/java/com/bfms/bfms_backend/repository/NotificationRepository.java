package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Notification;
import com.bfms.bfms_backend.repository.projection.NotificationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<NotificationProjection> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId, Pageable pageable);
}
