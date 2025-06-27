package com.bookstore.onlinebookstore.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String id;
    private User user;
    private List<OrderItem> items;
    private double total;
    private LocalDateTime timestamp;
    private String status;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;

    public Order() {
        this.timestamp = LocalDateTime.now();
        this.status = "Pending";
    }

    public Order(int id, User user, List<OrderItem> items, double total) {
        this.id = String.valueOf(id);
        this.user = user;
        this.items = items;
        this.total = total;
        this.timestamp = LocalDateTime.now();
        this.status = "Pending";
    }

    public String getId() { 
        return id; 
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<OrderItem> getItems() { 
        return items; 
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public double getTotal() { 
        return total; 
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 