package com.bookstore.onlinebookstore.controller;

import com.bookstore.onlinebookstore.model.Book;
import com.bookstore.onlinebookstore.model.BookInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Controller
@RequestMapping("/")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookInventory bookInventory;

    @Autowired
    public BookController(BookInventory bookInventory) {
        this.bookInventory = bookInventory;
    }

    @GetMapping("/books")
    public String browseAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model) {
        try {
            logger.info("Browsing all books - page: {}, sort: {}, dir: {}, search: {}", page, sort, dir, search);
            List<Book> allBooks = bookInventory.getAllBooksSorted(sort, dir);
            
            // Apply search filter if search parameter is provided
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase().trim();
                allBooks = allBooks.stream()
                    .filter(book -> 
                        book.getTitle().toLowerCase().contains(searchLower) ||
                        book.getAuthor().toLowerCase().contains(searchLower) ||
                        book.getCategory().toLowerCase().contains(searchLower)
                    )
                    .toList();
            }
            
            int totalBooks = allBooks.size();
            int booksPerPage = 16;
            int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage);
            int startIndex = (page - 1) * booksPerPage;
            int endIndex = Math.min(startIndex + booksPerPage, totalBooks);
            List<Book> booksForPage = allBooks.subList(startIndex, endIndex);
            model.addAttribute("books", booksForPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageTitle", search != null && !search.trim().isEmpty() ? "Search Results" : "All Books");
            model.addAttribute("sort", sort == null ? "" : sort);
            model.addAttribute("dir", dir == null ? "asc" : dir);
            model.addAttribute("search", search);
            return "books";
        } catch (Exception e) {
            logger.error("Error browsing all books", e);
            model.addAttribute("error", "An error occurred while loading books");
            return "error";
        }
    }

    @GetMapping("/category/{category}")
    public String browseByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "asc") String dir,
            Model model) {
        try {
            logger.info("Browsing category: {} - page: {}, sort: {}, dir: {}", category, page, sort, dir);
            List<Book> categoryBooks = bookInventory.getBooksByCategorySorted(category, sort, dir);
            if (categoryBooks.isEmpty()) {
                logger.warn("No books found for category: {}", category);
                model.addAttribute("error", "No books found in this category");
                return "error";
            }
            int totalBooks = categoryBooks.size();
            int booksPerPage = 16;
            int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage);
            int startIndex = (page - 1) * booksPerPage;
            int endIndex = Math.min(startIndex + booksPerPage, totalBooks);
            List<Book> booksForPage = categoryBooks.subList(startIndex, endIndex);
            model.addAttribute("books", booksForPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageTitle", category + " Books");
            model.addAttribute("sort", sort == null ? "" : sort);
            model.addAttribute("dir", dir == null ? "asc" : dir);
            return "books";
        } catch (Exception e) {
            logger.error("Error browsing category: {}", category, e);
            model.addAttribute("error", "An error occurred while loading books");
            return "error";
        }
    }

    @GetMapping("/book/{id}")
    public String viewBookDetails(@PathVariable String id, Model model) {
        try {
            logger.info("Viewing book details for ID: {}", id);
            Book book = bookInventory.getBookById(Integer.parseInt(id));
            if (book == null) {
                logger.warn("Book not found with ID: {}", id);
                model.addAttribute("error", "Book not found");
                return "error";
            }
            // Related books: same category, excluding this book
            var relatedCategory = bookInventory.getBooksByCategorySorted(book.getCategory(), "", "asc").stream()
                .filter(b -> b.getId() != book.getId())
                .limit(10)
                .toList();
            // Related books: same author, excluding this book
            var relatedAuthor = bookInventory.getAllBooksSorted("", "asc").stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(book.getAuthor()) && b.getId() != book.getId())
                .limit(10)
                .toList();
            model.addAttribute("book", book);
            model.addAttribute("relatedCategory", relatedCategory);
            model.addAttribute("relatedAuthor", relatedAuthor);
            return "book-details";
        } catch (Exception e) {
            logger.error("Error viewing book details for ID: {}", id, e);
            model.addAttribute("error", "An error occurred while loading book details");
            return "error";
        }
    }

    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public List<String> getSearchSuggestions(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            String searchLower = query.toLowerCase().trim();
            List<Book> allBooks = bookInventory.getAllBooksSorted("", "asc");
            
            // Get unique suggestions from titles, authors, and categories
            Set<String> suggestions = new HashSet<>();
            
            allBooks.stream()
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(searchLower) ||
                    book.getAuthor().toLowerCase().contains(searchLower) ||
                    book.getCategory().toLowerCase().contains(searchLower)
                )
                .forEach(book -> {
                    // Add title suggestions
                    if (book.getTitle().toLowerCase().contains(searchLower)) {
                        suggestions.add(book.getTitle());
                    }
                    // Add author suggestions
                    if (book.getAuthor().toLowerCase().contains(searchLower)) {
                        suggestions.add(book.getAuthor());
                    }
                    // Add category suggestions
                    if (book.getCategory().toLowerCase().contains(searchLower)) {
                        suggestions.add(book.getCategory());
                    }
                });
            
            // Convert to list and limit to 10 suggestions
            return suggestions.stream()
                .limit(10)
                .sorted()
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error getting search suggestions", e);
            return new ArrayList<>();
        }
    }
} 