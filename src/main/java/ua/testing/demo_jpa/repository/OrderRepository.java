package ua.testing.demo_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.testing.demo_jpa.entity.Order;
import ua.testing.demo_jpa.entity.OrderStatus;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);
}
