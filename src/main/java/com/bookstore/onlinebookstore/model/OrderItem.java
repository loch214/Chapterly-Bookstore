package com.bookstore.onlinebookstore.model;

public class OrderItem {
    private int id;
    private Book book;
    private int quantity;
    private double price;

    public OrderItem() {
    }

    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.price = book.getPrice();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSubtotal() {
        return price * quantity;
    }
} 