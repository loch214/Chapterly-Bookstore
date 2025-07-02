package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.model.BookInventory;
import com.bookstore.onlinebookstore.model.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private BookInventory bookInventory;

    @GetMapping
    public String viewCart(Model model, HttpSession session,
                          @RequestParam(required = false) String buyNow,
                          @RequestParam(required = false) String bookId,
                          @RequestParam(required = false) String quantity) {
        logger.info("viewCart: buyNow={}, bookId={}, quantity={}", buyNow, bookId, quantity);
        Object user = session.getAttribute("user");
        if (user == null) {
            logger.info("viewCart: user not logged in, redirecting to login");
            return "redirect:/login";
        }
        
        // Handle buy now scenario
        if ("true".equals(buyNow) && bookId != null && quantity != null) {
            try {
                int bookIdInt = Integer.parseInt(bookId);
                int quantityInt = Integer.parseInt(quantity);
                logger.info("viewCart: buyNow flow, adding bookId={} quantity={}", bookIdInt, quantityInt);
                // Add the book to cart with buyNow=true
                String result = addToCartInternal(bookIdInt, quantityInt, "true", session);
                logger.info("viewCart: addToCartInternal result={}", result);
                if ("buy_now_success".equals(result)) {
                    logger.info("viewCart: redirecting to /order/checkout");
                    // Redirect to checkout
                    return "redirect:/order/checkout";
                }
            } catch (NumberFormatException e) {
                logger.error("viewCart: invalid bookId or quantity", e);
            }
        }
        
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        model.addAttribute("cart", cart);
        return "cart";
    }

    private String addToCartInternal(int bookId, int quantity, String buyNow, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "not_logged_in";
        }
        Book book = bookInventory.getBookById(bookId);
        if (book != null) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }
            
            // Clear cart if this is a buy now action
            if ("true".equals(buyNow)) {
                cart.clear();
            }
            
            cart.addItem(book, quantity);
            
            // If this is buy now, return a special response
            if ("true".equals(buyNow)) {
                return "buy_now_success";
            }
            
            return "success";
        }
        return "error";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateQuantity(@RequestParam int bookId, @RequestParam int quantity, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "not_logged_in";
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.updateQuantity(bookId, quantity);
            return "success";
        }
        return "error";
    }

    @PostMapping("/remove")
    @ResponseBody
    public String removeItem(@RequestParam int bookId, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return "not_logged_in";
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(bookId);
            return "success";
        }
        return "error";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return 0;
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            return cart.getItems().size();
        }
        return 0;
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam int bookId, @RequestParam int quantity, 
                           @RequestParam(required = false) String buyNow, 
                           HttpSession session) {
        return addToCartInternal(bookId, quantity, buyNow, session);
    }
} 