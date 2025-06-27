package com.bookstore.onlinebookstore.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private int bookId;
    private LocalDateTime addedAt;
    private Book book; // Reference to the actual book

    public Wishlist() {}

    public Wishlist(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.addedAt = LocalDateTime.now();
    }

    public Wishlist(int id, int userId, int bookId, LocalDateTime addedAt) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
} 