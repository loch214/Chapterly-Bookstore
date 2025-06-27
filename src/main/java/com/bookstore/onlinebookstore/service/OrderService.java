package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.Cart;
import com.bookstore.onlinebookstore.model.Order;
import com.bookstore.onlinebookstore.model.OrderItem;
import com.bookstore.onlinebookstore.model.User;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final List<Order> orders = new ArrayList<>();
    
    @Autowired
    private OrderIdGenerator orderIdGenerator;

    public Order createOrder(User user, Cart cart) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> new OrderItem(cartItem.getBook(), cartItem.getQuantity()))
                .collect(Collectors.toList());

        Order order = new Order();
        order.setId(orderIdGenerator.generateNextId());
        order.setUser(user);
        order.setItems(orderItems);
        order.setTotal(cart.getTotal());
        order.setStatus("Confirmed");

        orders.add(order);
        return order;
    }

    public List<Order> getOrdersByUser(User user) {
        return orders.stream()
                .filter(order -> order.getUser().getEmail().equals(user.getEmail()))
                .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
                .collect(Collectors.toList());
    }

    public Order getOrderById(String orderId) {
        return orders.stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public void saveOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orders.stream().filter(o -> o.getUser().getId() == userId).collect(Collectors.toList());
    }
} 