package com.bookstore.onlinebookstore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String author;
    private String category;
    private double price;
    private double rating;
    private int stock;
    private String description;
    private String imageUrl;
    private String longDescription;
    private String authorBio;
} 