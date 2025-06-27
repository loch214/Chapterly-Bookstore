package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.model.BookInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BookInventory bookInventory;

    @GetMapping("/")
    public String home(Model model) {
        // Get featured books (you can implement your own logic for featured books)
        List<Book> featuredBooks = bookInventory.getAllBooks().subList(0, 
            Math.min(8, bookInventory.getAllBooks().size()));
        
        model.addAttribute("featuredBooks", featuredBooks);
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
} 