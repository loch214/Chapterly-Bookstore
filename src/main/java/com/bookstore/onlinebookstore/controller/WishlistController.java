package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.model.Wishlist;
import com.bookstore.onlinebookstore.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public String viewWishlist(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Wishlist> wishlist = wishlistService.getUserWishlist(user.getId());
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("user", user);
        return "wishlist";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToWishlist(@RequestParam int bookId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "not_logged_in";
        }
        boolean success = wishlistService.addToWishlist(user.getId(), bookId);
        return success ? "success" : "error";
    }

    @PostMapping("/remove")
    @ResponseBody
    public String removeFromWishlist(@RequestParam int bookId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "not_logged_in";
        }
        boolean success = wishlistService.removeFromWishlist(user.getId(), bookId);
        return success ? "success" : "error";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getWishlistCount(HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null || !(userObj instanceof User)) {
            return 0;
        }
        User user = (User) userObj;
        return wishlistService.getWishlistCount(user.getId());
    }

    @GetMapping("/check")
    @ResponseBody
    public boolean isInWishlist(@RequestParam int bookId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return wishlistService.isInWishlist(user.getId(), bookId);
    }
} 