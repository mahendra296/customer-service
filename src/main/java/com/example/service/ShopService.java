package com.example.service;

import com.example.dto.CustomerOrder;
import com.example.dto.ItemRequest;
import com.example.dto.MenuPageResponse;
import com.example.dto.MenuResponse;
import com.example.dto.OrderRequest;
import com.example.dto.OrderStatusType;
import com.example.dto.PagingInformation;
import com.example.dto.ShopPageResponse;
import com.example.dto.ShopResponse;
import com.example.entity.Menu;
import com.example.entity.MenuItem;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Shop;
import com.example.exceptions.InternalServerException;
import com.example.exceptions.InvalidRequestException;
import com.example.exceptions.ResourceNotFoundException;
import com.example.mapper.ModelMapper;
import com.example.repository.MenuItemRepository;
import com.example.repository.MenuRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ShopRepository;
import com.example.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;

    public ShopPageResponse getAllShops(Integer radius, BigDecimal latitude, BigDecimal longitude, Integer pageNumber, Integer pageSize) {
        log.info("Invoke getAllShops method.");

        PageRequest pageRequest = PageRequest.of(CommonUtil.normalizePageNumber(pageNumber), CommonUtil.normalizePageSize(pageSize));

        Page<Shop> pageableShops = shopRepository.getAllShops(radius, latitude, longitude, pageRequest);
        List<ShopResponse> shopResponseList = null;
        if (pageableShops != null) {
            log.info("Pageable response is not empty.");
            shopResponseList = pageableShops.getContent().stream().map(ModelMapper::convertToShopResponse).toList();
            log.info("End getAllShops method.");
            return ShopPageResponse.builder().data(shopResponseList).paging(PagingInformation.builder().pageNumber(pageableShops.getNumber()).pageSize(pageableShops.getSize()).totalRecords(pageableShops.getTotalElements()).build()).build();
        }
        return ShopPageResponse.builder().data(Collections.emptyList()).paging(PagingInformation.builder().pageNumber(pageNumber).pageSize(pageSize).totalRecords(0L).build()).build();
    }

    public MenuPageResponse getMenuByShopId(Long shopId, Integer pageNumber, Integer pageSize) {
        log.info("Invoke getMenuByShopId method.");
        checkIfShopExists(shopId);

        PageRequest pageRequest = PageRequest.of(CommonUtil.normalizePageNumber(pageNumber), CommonUtil.normalizePageSize(pageSize));

        Page<Menu> pageableMenu = menuRepository.getMenuByShopId(shopId, pageRequest);
        List<MenuResponse> menuResponseList = null;
        if (pageableMenu != null) {
            log.info("Pageable response is not empty.");
            menuResponseList = pageableMenu.getContent().stream().map(ModelMapper::convertToMenuResponse).toList();
            log.info("End getMenuByShopId method.");
            return MenuPageResponse.builder().data(menuResponseList).paging(PagingInformation.builder().pageNumber(pageableMenu.getNumber()).pageSize(pageableMenu.getSize()).totalRecords(pageableMenu.getTotalElements()).build()).build();
        }
        return MenuPageResponse.builder().data(Collections.emptyList()).paging(PagingInformation.builder().pageNumber(pageNumber).pageSize(pageSize).totalRecords(0L).build()).build();
    }

    private void checkIfShopExists(Long shopId) {
        log.info("Invoke checkIfShopExists method.");
        boolean isExists = shopRepository.existsById(shopId);
        if (!isExists) {
            throw new ResourceNotFoundException("Shop not found by shopId : " + shopId);
        }
    }

    public CustomerOrder createOrder(Long shopId, OrderRequest orderRequest) {
        log.info("Invoke createOrder method.");
        Shop shop = validateOrderRequest(shopId, orderRequest);
        List<Long> itemIds = orderRequest.getMenuItems().stream().map(ItemRequest::getItemId).toList();
        List<MenuItem> menuItemList = menuItemRepository.findByIdIn(itemIds);
        Map<Long, MenuItem> menuItemMap = menuItemList.stream().collect(Collectors.toMap(MenuItem::getId, x -> x));
        BigDecimal totalAmount = getTotalAmount(orderRequest.getMenuItems(), menuItemMap);
        Order order = Order.builder().customerId(orderRequest.getCustomerId()).menuId(orderRequest.getMenuId()).status(OrderStatusType.OPEN).shop(shop).total(totalAmount).build();

        Set<OrderItem> orderItemList = mapToOrderItems(orderRequest.getMenuItems(), menuItemMap, order);
        order.setOrderItems(orderItemList);
        saveOrder(order);
        updateMenuItemQuantity(orderRequest.getMenuItems(), menuItemList);
        log.info("Invoke createOrder method.");
        CustomerOrder customerOrder = ModelMapper.convertToCustomerOrder(order);
        return customerOrder;
    }

    private void updateMenuItemQuantity(List<ItemRequest> menuItems, List<MenuItem> menuItemList) {
        Map<Long, Integer> itemRequestMap = menuItems.stream().collect(Collectors.toMap(ItemRequest::getItemId, ItemRequest::getQuantity));
        menuItemList.forEach(menuItem -> {
            Integer quantity = itemRequestMap.get(menuItem.getId());
            menuItem.setCurrentQuantity(menuItem.getCurrentQuantity() - quantity);
        });
        menuItemRepository.saveAll(menuItemList);
    }

    private Set<OrderItem> mapToOrderItems(List<ItemRequest> menuItems, Map<Long, MenuItem> menuItemMap, Order order) {
        log.info("Invoke mapToOrderItems method.");
        Set<OrderItem> orderItemList = new HashSet<>();
        menuItems.forEach(itemRequest -> orderItemList.add(OrderItem.builder().menuItemId(itemRequest.getItemId()).price(menuItemMap.get(itemRequest.getItemId()).getPrice()).quantity(itemRequest.getQuantity()).orders(order).build()));
        log.info("End mapToOrderItems method.");
        return orderItemList;
    }

    private BigDecimal getTotalAmount(List<ItemRequest> menuItems, Map<Long, MenuItem> menuItemMap) {
        log.info("Invoke getTotalAmount method.");
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.valueOf(0));
        menuItems.forEach(itemRequest -> {
            MenuItem menuItem = menuItemMap.get(itemRequest.getItemId());
            BigDecimal value = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal amount = BigDecimal.valueOf(totalAmount.get().doubleValue() + value.doubleValue());
            totalAmount.set(amount);
        });
        return totalAmount.get();
    }

    private void saveOrder(Order order) {
        try {
            orderRepository.save(order);
        } catch (Exception ex) {
            log.error("Exception while save order", ex);
            throw new InternalServerException("Exception while save order.");
        }
    }

    private Shop validateOrderRequest(Long shopId, OrderRequest orderRequest) {
        log.info("Invoke validateOrderRequest method.");
        if (orderRequest == null) {
            throw new InvalidRequestException("Order request is null.");
        }
        if (!Objects.equals(shopId, orderRequest.getShopId())) {
            throw new InvalidRequestException("shopId is mismatch with request.");
        }
        if (CollectionUtils.isEmpty(orderRequest.getMenuItems())) {
            throw new InvalidRequestException("Items are not present.");
        }
        Shop shop = shopRepository.findById(orderRequest.getShopId()).orElseThrow(() -> new ResourceNotFoundException("Shop not found by id: " + shopId));
        log.info("End validateOrderRequest method.");
        return shop;
    }

    public void updateOrderStatus(Long orderId, String status) {
        log.info("Invoke updateOrderStatus method.");
        OrderStatusType statusType = OrderStatusType.fromValue(status);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found by orderId : " + orderId);
        }
        orderRepository.updateStatus(orderId, statusType);
        log.info("End updateOrderStatus method.");
    }
}
