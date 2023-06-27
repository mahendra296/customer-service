package com.example.service;

import com.example.dto.CustomerOrder;
import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.dto.OrderPageResponse;
import com.example.dto.PagingInformation;
import com.example.entity.Customer;
import com.example.entity.Order;
import com.example.exceptions.InvalidRequestException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mapper.ModelMapper;
import com.example.repository.CustomerRepository;
import com.example.repository.OrderRepository;
import com.example.utils.CommonUtil;
import com.example.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomUserDetailService customUserDetailService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public LoginResponse signIn(LoginRequest loginRequest) {
        log.info("Invoke signIn Method.");
        validateUserSignInRequest(loginRequest);
        final UserDetails userDetails = customUserDetailService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        log.info("End signIn Method.");
        return LoginResponse.builder().token(token).build();
    }

    private void validateUserSignInRequest(LoginRequest loginRequest) {
        log.info("Invoke validateUserSignInRequest method.");
        if (loginRequest == null || !StringUtils.isNoneBlank(loginRequest.getEmail())
                || !StringUtils.isNoneBlank(loginRequest.getPassword())) {
            throw new InvalidRequestException("Invalid sign in request.");
        }
        Customer customer = customerRepository.findByEmail(loginRequest.getEmail());
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found by email : " + loginRequest.getEmail());
        }
        boolean isValidPWD = BCrypt.checkpw(loginRequest.getPassword(), customer.getPassword());
        log.info("Is Password match : {}", isValidPWD);
        if (!isValidPWD) {
            throw new InvalidRequestException("Invalid credential.");
        }
        log.info("End validateUserSignInRequest method.");
    }

    public OrderPageResponse getCustomerOrders(Long customerId, Integer pageNumber, Integer pageSize) {
        log.info("Invoke getCustomerOrders Method.");
        if (customerId == null) {
            log.error("CustomerId is missing in request.");
            throw new InvalidRequestException("CustomerId is missing in request.");
        }
        PageRequest pageRequest = PageRequest.of(CommonUtil.normalizePageNumber(pageNumber), CommonUtil.normalizePageSize(pageSize));
        Page<Order> pageableOrders = orderRepository.findAllByCustomerId(customerId, pageRequest);
        if (pageableOrders != null) {
            log.info("Pageable response is not empty.");
            List<CustomerOrder> customerOrders = pageableOrders.getContent().stream().map(ModelMapper::convertToCustomerOrder).toList();
            log.info("End getAllShops method.");
            return OrderPageResponse.builder()
                    .data(customerOrders)
                    .paging(PagingInformation.builder()
                            .pageNumber(pageableOrders.getNumber())
                            .pageSize(pageableOrders.getSize())
                            .totalRecords(pageableOrders.getTotalElements())
                            .build())
                    .build();
        }
        log.info("End getCustomerOrders Method.");
        return OrderPageResponse.builder().data(Collections.emptyList()).paging(PagingInformation.builder().pageNumber(pageNumber).pageSize(pageSize).totalRecords(0L).build()).build();
    }

    public CustomerOrder getCustomerOrderByOrderId(Long customerId, Long orderId) {
        log.info("Invoke getCustomerOrderByOrderId Method.");
        if (customerId == null || orderId == null) {
            log.error("customerId or orderId is Null");
            throw new InvalidRequestException("CustomerId or OrderId is missing in input.");
        }
        log.info("Get customer order by CustomerId and orderId");
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId);
        CustomerOrder customerOrder = ModelMapper.convertToCustomerOrder(order);
        log.info("End getCustomerOrderByOrderId Method.");
        return customerOrder;
    }
}
