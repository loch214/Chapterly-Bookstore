package com.bookstore.onlinebookstore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
    private int id;
    private int userId;
    private String nickname;
    private String address;
    private String phoneNumber;
} 