package com.example.api;

import com.example.dto.CustomerOrder;
import com.example.dto.MenuPageResponse;
import com.example.dto.OrderRequest;
import com.example.dto.ShopPageResponse;
import com.example.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopsApiDelegateImpl implements ShopsApiDelegate {

    private final ShopService shopService;

    @Override
    public ResponseEntity<ShopPageResponse> getAllShops(Integer radius, BigDecimal latitude, BigDecimal longitude, Integer pageNumber, Integer pageSize) {
        log.info("Invoke getAllShops API.");
        ShopPageResponse shopPageResponse = shopService.getAllShops(radius, latitude, longitude, pageNumber, pageSize);
        log.info("End getAllShops API.");
        return ResponseEntity.ok().body(shopPageResponse);
    }

    @Override
    public ResponseEntity<MenuPageResponse> getMenuByShopId(Long shopId, Integer pageNumber, Integer pageSize) {
        log.info("Invoke getAllShops API.");
        MenuPageResponse menuPageResponse = shopService.getMenuByShopId(shopId, pageNumber, pageSize);
        log.info("End getAllShops API.");
        return ResponseEntity.ok().body(menuPageResponse);
    }

    @Override
    public ResponseEntity<CustomerOrder> createOrder(Long shopId, OrderRequest orderRequest) {
        log.info("Invoke createOrder API.");
        CustomerOrder customerOrder = shopService.createOrder(shopId, orderRequest);
        log.info("End createOrder API.");
        return ResponseEntity.ok().body(customerOrder);
    }
}
