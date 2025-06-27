package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.Cart;
import com.bookstore.onlinebookstore.model.Order;
import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.model.ContactInfo;
import com.bookstore.onlinebookstore.model.Wishlist;
import com.bookstore.onlinebookstore.service.ContactInfoService;
import com.bookstore.onlinebookstore.service.OrderService;
import com.bookstore.onlinebookstore.service.UserService;
import com.bookstore.onlinebookstore.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactInfoService contactInfoService;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        model.addAttribute("contacts", contactInfoService.getContactsByUserId(user.getId()));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(HttpSession session, Model model,
                                  @RequestParam(required = false) Integer contactId,
                                  @RequestParam(required = false) String address,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String paymentMethod,
                                  @RequestParam(required = false) String saveNickname) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        try {
            String shippingAddress = address;
            if (contactId != null) {
                ContactInfo contact = contactInfoService.getContactById(contactId);
                if (contact != null && contact.getUserId() == user.getId()) {
                    shippingAddress = contact.getAddress();
                    phoneNumber = contact.getPhoneNumber();
                }
            }
            Order order = orderService.createOrder(user, cart);
            order.setShippingAddress(shippingAddress);
            order.setPhoneNumber(phoneNumber);
            order.setPaymentMethod(paymentMethod);
            
            if (saveNickname != null && !saveNickname.trim().isEmpty() && address != null && !address.trim().isEmpty()) {
                ContactInfo contact = new ContactInfo(0, user.getId(), saveNickname, address, phoneNumber);
                contactInfoService.addContact(contact);
            }

            session.removeAttribute("cart"); // Clear cart after successful order
            model.addAttribute("order", order);
            return "order-confirmation";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to process order: " + e.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        List<Wishlist> wishlist = wishlistService.getUserWishlist(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("contacts", contactInfoService.getContactsByUserId(user.getId()));
        model.addAttribute("wishlist", wishlist);
        return "profile";
    }

    @GetMapping("/history")
    public String orderHistory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order-history";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable String orderId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUser().getEmail().equals(user.getEmail())) {
            return "redirect:/order/profile";
        }

        model.addAttribute("order", order);
        return "order-details";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String currentPassword,
                               @RequestParam(required = false) String newPassword,
                               @RequestParam(required = false) String confirmPassword,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in.");
            return "redirect:/login";
        }
        // Check current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("errorInModal", "Current password is incorrect.");
            model.addAttribute("showEditProfileModal", true);
            // Repopulate model attributes for profile page
            model.addAttribute("user", user);
            model.addAttribute("orders", orderService.getOrdersByUser(user));
            model.addAttribute("contacts", contactInfoService.getContactsByUserId(user.getId()));
            model.addAttribute("wishlist", wishlistService.getUserWishlist(user.getId()));
            return "profile";
        }
        // If changing password, check new password and confirmation
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("errorInModal", "New passwords do not match.");
                model.addAttribute("showEditProfileModal", true);
                model.addAttribute("user", user);
                model.addAttribute("orders", orderService.getOrdersByUser(user));
                model.addAttribute("contacts", contactInfoService.getContactsByUserId(user.getId()));
                model.addAttribute("wishlist", wishlistService.getUserWishlist(user.getId()));
                return "profile";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        user.setUsername(username);
        user.setEmail(email);
        userService.saveUsersToFile();
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/order/profile";
    }
} 