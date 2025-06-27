package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                        @RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String confirmPassword,
                        RedirectAttributes redirectAttributes) {
        // Validate input
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/signup";
        }

        if (userService.usernameExists(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/signup";
        }

        if (userService.emailExists(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/signup";
        }

        // Create new user
        User user = new User();
        user.setId(userService.getNextId());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setEnabled(true);

        userService.addUser(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/auth/status")
    @ResponseBody
    public Map<String, Boolean> getAuthStatus(HttpSession session) {
        boolean loggedIn = session.getAttribute("user") != null;
        return Collections.singletonMap("loggedIn", loggedIn);
    }

    @GetMapping("/check")
    @ResponseBody
    public String checkAuth(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() ? "true" : "false";
    }

    @PostMapping("/auth/save-intent")
    @ResponseBody
    public Map<String, String> saveIntent(@RequestParam String action, 
                                         @RequestParam(required = false) String bookId,
                                         @RequestParam(required = false) String quantity,
                                         @RequestParam(required = false) String currentPage,
                                         HttpSession session) {
        System.out.println("Save intent called with action: " + action + ", bookId: " + bookId + ", quantity: " + quantity + ", currentPage: " + currentPage); // Debug
        
        // Save the action details instead of just redirect URL
        session.setAttribute("pendingAction", action);
        session.setAttribute("pendingBookId", bookId);
        session.setAttribute("pendingQuantity", quantity);
        session.setAttribute("pendingCurrentPage", currentPage);
        
        System.out.println("Saved pending action: " + action); // Debug
        return Collections.singletonMap("status", "saved");
    }
} 