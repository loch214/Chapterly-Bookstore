package com.bookstore.onlinebookstore.model;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookInventory {
    private static final Logger logger = LoggerFactory.getLogger(BookInventory.class);
    private LinkedList<Book> books;

    public BookInventory() {
        this.books = new LinkedList<>();
    }

    @PostConstruct
    public void loadBooksFromFile() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("books.txt");
            if (is == null) {
                logger.error("Could not find books.txt file in resources");
                return;
            }
            try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String[] parts;
            boolean firstLine = true;
            while ((parts = reader.readNext()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (parts.length < 11) {
                    logger.warn("Invalid book entry, not enough parts: {}", Arrays.toString(parts));
                    continue;
                }
                try {
                    Book book = new Book(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3],
                        Double.parseDouble(parts[4]),
                        Double.parseDouble(parts[5]),
                        Integer.parseInt(parts[6]),
                        parts[7],
                        parts[8],
                        parts[9],
                        parts[10]
                    );
                    books.add(book);
                } catch (Exception e) {
                    logger.error("Error parsing book entry: {}", Arrays.toString(parts), e);
                }
            }
            logger.info("Loaded {} books from file", books.size());
            }
        } catch (Exception e) {
            logger.error("Error loading books from file", e);
        }
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(int bookId) {
        books.removeIf(book -> book.getId() == bookId);
    }

    public Book getBookById(int bookId) {
        return books.stream()
                .filter(book -> book.getId() == bookId)
                .findFirst()
                .orElse(null);
    }

    public LinkedList<Book> getAllBooks() {
        return new LinkedList<>(books);
    }

    public List<Book> getAllBooksSorted(String sort, String dir) {
        return books.stream()
            .sorted(getComparator(sort, dir))
            .toList();
    }

    public List<Book> getBooksByCategorySorted(String category, String sort, String dir) {
        return books.stream()
            .filter(book -> book.getCategory().equalsIgnoreCase(category))
            .sorted(getComparator(sort, dir))
            .toList();
    }

    private Comparator<Book> getComparator(String sort, String dir) {
        Comparator<Book> comp = Comparator.comparing(Book::getTitle);
        if (sort == null || sort.isEmpty()) return comp;
        switch (sort) {
            case "price":
                comp = Comparator.comparingDouble(Book::getPrice);
                break;
            case "rating":
                comp = Comparator.comparingDouble(Book::getRating);
                break;
            case "title":
                comp = Comparator.comparing(Book::getTitle);
                break;
        }
        if (dir != null && dir.equals("desc")) {
            comp = comp.reversed();
        }
        return comp;
    }

    public List<Book> getBooksByCategory(String category) {
        logger.info("Getting books for category: {}", category);
        return books.stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
} 