package com.example.api;

import com.example.dto.CustomerOrder;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerApiDelegateImpl implements CustomersApiDelegate {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<LoginResponse> signIn(LoginRequest loginRequest) {
        log.info("Invoke signIn API.");
        LoginResponse loginResponse = customerService.signIn(loginRequest);
        log.info("End signIn API.");
        return ResponseEntity.ok().body(loginResponse);
    }

    @Override
    public ResponseEntity<List<CustomerOrder>> getCustomerOrders(Long customerId) {
        log.info("Invoke getCustomerOrders API.");
        List<CustomerOrder> customerOrderList = customerService.getCustomerOrders(customerId);
        log.info("End getCustomerOrders API.");
        return ResponseEntity.ok().body(customerOrderList);
    }

    @Override
    public ResponseEntity<CustomerOrder> getCustomerOrderByOrderId(Long customerId, Long orderId) {
        log.info("Invoke getCustomerOrderByOrderId API.");
        CustomerOrder customerOrder = customerService.getCustomerOrderByOrderId(customerId, orderId);
        log.info("End getCustomerOrderByOrderId API.");
        return ResponseEntity.ok().body(customerOrder);
    }
}
