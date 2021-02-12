package ua.testing.demo_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.Order;
import ua.testing.demo_jpa.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

}
