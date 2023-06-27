package com.example.mapper;

import com.example.dto.CustomerOrder;
import com.example.dto.CustomerOrderItem;
import com.example.dto.MenuResponse;
import com.example.dto.ShopResponse;
import com.example.entity.Menu;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ModelMapper {

    public static ShopResponse convertToShopResponse(Shop shop) {
        ShopResponse shopResponse = null;
        if (shop != null) {
            shopResponse = ShopResponse.builder()
                    .id(shop.getId())
                    .name(shop.getName())
                    .status(shop.getStatus().toString())
                    .latitude(BigDecimal.valueOf(shop.getLatitude()))
                    .longitude(BigDecimal.valueOf(shop.getLongitude()))
                    .build();
        }
        return shopResponse;
    }

    public static MenuResponse convertToMenuResponse(Menu menu) {
        MenuResponse menuResponse = null;
        if (menu != null) {
            menuResponse = MenuResponse.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .build();
        }
        return menuResponse;
    }

    public static CustomerOrder convertToCustomerOrder(Order order) {
        CustomerOrder customerOrder = null;
        if (order != null) {
            customerOrder = CustomerOrder.builder()
                    .id(order.getId())
                    .shopId(order.getShop().getId())
                    .customerId(order.getCustomerId())
                    .menuId(order.getMenuId())
                    .orderStatus(order.getStatus())
                    .totalAmount(order.getTotal())
                    .orderItems(convertToCustomerOrderItemList(order.getOrderItems()))
                    .build();
        }
        return customerOrder;
    }

    private static List<CustomerOrderItem> convertToCustomerOrderItemList(Set<OrderItem> orderItems) {
        List<CustomerOrderItem> customerOrderItemList = null;
        if (orderItems != null) {
            customerOrderItemList = orderItems.stream().map(it -> convertToCustomerOrderItem(it)).toList();
        }
        return customerOrderItemList;
    }

    private static CustomerOrderItem convertToCustomerOrderItem(OrderItem orderItem) {
        CustomerOrderItem customerOrderItem = null;
        if (orderItem != null) {
            customerOrderItem = CustomerOrderItem.builder()
                    .id(orderItem.getId())
                    .menuItemId(orderItem.getMenuItemId())
                    .price(orderItem.getPrice())
                    .quntity(orderItem.getQuantity())
                    .build();
        }
        return customerOrderItem;
    }
}
