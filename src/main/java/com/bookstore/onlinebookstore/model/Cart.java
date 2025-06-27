package com.bookstore.onlinebookstore.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private double total;

    public Cart() {
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
    }

    public void addItem(Book book, int quantity) {
        CartItem existingItem = items.stream()
                .filter(item -> item.getBook().getId() == book.getId())
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.add(new CartItem(book, quantity));
        }
        calculateTotal();
    }

    public void removeItem(int bookId) {
        items.removeIf(item -> item.getBook().getId() == bookId);
        calculateTotal();
    }

    public void updateQuantity(int bookId, int quantity) {
        items.stream()
                .filter(item -> item.getBook().getId() == bookId)
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    calculateTotal();
                });
    }

    public void clear() {
        items.clear();
        calculateTotal();
    }

    private void calculateTotal() {
        this.total = getSubtotal();
    }
} 