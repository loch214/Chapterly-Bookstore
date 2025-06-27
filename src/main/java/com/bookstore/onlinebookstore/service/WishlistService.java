package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.model.BookInventory;
import com.bookstore.onlinebookstore.model.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class WishlistService {
    private static final String WISHLIST_FILE = "wishlist.txt";
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private final BookInventory bookInventory;

    @Autowired
    public WishlistService(BookInventory bookInventory) {
        this.bookInventory = bookInventory;
        initializeWishlistFile();
    }

    private void initializeWishlistFile() {
        Path path = Paths.get(WISHLIST_FILE);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                // Log this properly in a real app
                e.printStackTrace();
            }
        }
    }

    public boolean addToWishlist(int userId, int bookId) {
        Book book = bookInventory.getBookById(bookId);
        if (book == null) {
            return false;
        }

        if (isInWishlist(userId, bookId)) {
            return false;
        }

        Wishlist wishlistItem = new Wishlist(userId, bookId);
        wishlistItem.setId(idCounter.getAndIncrement());
        
        return saveWishlistItem(wishlistItem);
    }

    public boolean removeFromWishlist(int userId, int bookId) {
        List<Wishlist> wishlist = loadWishlist();
        List<Wishlist> updatedWishlist = wishlist.stream()
                .filter(item -> !(item.getUserId() == userId && item.getBookId() == bookId))
                .collect(Collectors.toList());

        if (wishlist.size() == updatedWishlist.size()) {
            return false;
        }

        return saveWishlist(updatedWishlist);
    }

    public List<Wishlist> getUserWishlist(int userId) {
        List<Wishlist> wishlist = loadWishlist();
        List<Wishlist> userWishlist = wishlist.stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());

        for (Wishlist item : userWishlist) {
            Book book = bookInventory.getBookById(item.getBookId());
            item.setBook(book);
        }

        return userWishlist;
    }

    public boolean isInWishlist(int userId, int bookId) {
        List<Wishlist> wishlist = loadWishlist();
        return wishlist.stream()
                .anyMatch(item -> item.getUserId() == userId && item.getBookId() == bookId);
    }

    public int getWishlistCount(int userId) {
        List<Wishlist> wishlist = loadWishlist();
        return (int) wishlist.stream()
                .filter(item -> item.getUserId() == userId)
                .count();
    }

    private List<Wishlist> loadWishlist() {
        List<Wishlist> wishlist = new ArrayList<>();
        Path path = Paths.get(WISHLIST_FILE);

        if (!Files.exists(path)) {
            return wishlist;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        int id = Integer.parseInt(parts[0]);
                        int userId = Integer.parseInt(parts[1]);
                        int bookId = Integer.parseInt(parts[2]);
                        LocalDateTime addedAt = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        
                        Wishlist item = new Wishlist(id, userId, bookId, addedAt);
                        wishlist.add(item);
                        
                        if (id >= idCounter.get()) {
                            idCounter.set(id + 1);
                        }
                    }
                } catch (Exception e) {
                    // Log parsing errors
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Log file loading errors
            e.printStackTrace();
        }

        return wishlist;
    }

    private boolean saveWishlistItem(Wishlist item) {
        List<Wishlist> wishlist = loadWishlist();
        wishlist.add(item);
        return saveWishlist(wishlist);
    }

    private boolean saveWishlist(List<Wishlist> wishlist) {
        Path path = Paths.get(WISHLIST_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Wishlist item : wishlist) {
                String line = String.format("%d|%d|%d|%s%n",
                        item.getId(),
                        item.getUserId(),
                        item.getBookId(),
                        item.getAddedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                writer.write(line);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
} 