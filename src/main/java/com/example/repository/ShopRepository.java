package com.example.repository;

import com.example.entity.Menu;
import com.example.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query(value = "SELECT * FROM shop s " +
            "WHERE ((?1 is null) OR (SQRT( POW(69.1 * (s.latitude - ?2), 2) + POW(69.1 * (?3 - s.longitude) * COS(s.latitude / 57.3), 2))  < ?1))", nativeQuery = true)
    Page<Shop> getAllShops(Integer radius, BigDecimal latitude, BigDecimal longitude, PageRequest pageRequest);
}
