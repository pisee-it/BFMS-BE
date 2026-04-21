package repository;

import entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {
    // 1. Phục vụ việc kiểm tra lịch trình của tuyến trong ngày
}
