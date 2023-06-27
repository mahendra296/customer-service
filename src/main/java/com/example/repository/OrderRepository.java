package com.example.repository;

import com.example.dto.OrderStatusType;
import com.example.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByCustomerIdAndId(Long customerId, Long orderId);

    Page<Order> findAllByCustomerId(Long customerId, PageRequest pageRequest);

    @Query("update Order set status = :status where id = :orderId")
    @Modifying
    @Transactional
    void updateStatus(Long orderId, OrderStatusType status);
}
