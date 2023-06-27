package com.example.repository;

import com.example.dto.OrderStatusType;
import com.example.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByCustomerIdAndId(Long customerId, Long orderId);

    List<Order> findAllByCustomerId(Long customerId);

    @Query("update Order set status = :status where id = :orderId")
    @Modifying
    @Transactional
    void updateStatus(Long orderId, OrderStatusType status);
}
