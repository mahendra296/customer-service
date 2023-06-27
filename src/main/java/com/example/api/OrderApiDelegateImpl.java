package com.example.api;

import com.example.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApiDelegateImpl implements OrdersApiDelegate {

    private final ShopService shopService;

    @Override
    public ResponseEntity<Void> updateOrderStatus(Long orderId, String status) {
        log.info("Invoke updateOrderStatus API.");
        shopService.updateOrderStatus(orderId, status);
        log.info("End updateOrderStatus API.");
        return ResponseEntity.ok().build();
    }
}
