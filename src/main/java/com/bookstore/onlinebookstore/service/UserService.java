package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class UserService {
    private static final String FILE_PATH = "data/users.txt";
    private final List<User> users = new CopyOnWriteArrayList<>();
    private long nextId = 1;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        File file = new File(FILE_PATH);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Add a default admin user if the file is new
                if (users.isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                    admin.setEmail("admin@example.com");
                    admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                admin.setEnabled(true);
                    addUser(admin);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadUsersFromFile();
        }
    }
    
    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    User user = new User();
                    user.setId(Integer.parseInt(parts[0]));
                    user.setUsername(parts[1]);
                    user.setEmail(parts[2]);
                    user.setPassword(parts[3]);
                    user.setRole(parts[4]);
                    user.setEnabled(Boolean.parseBoolean(parts[5]));
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nextId = users.stream().mapToLong(User::getId).max().orElse(0) + 1;
    }

    public synchronized void addUser(User user) {
        user.setId((int) nextId++);
        users.add(user);
        saveUsersToFile();
    }

    public void saveUsersToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                out.println(user.getId() + "," + user.getUsername() + "," + user.getEmail() + "," + user.getPassword() + "," + user.getRole() + "," + user.isEnabled());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public boolean usernameExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public boolean emailExists(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public int getNextId() {
        return (int) nextId;
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return users.stream()
                .filter(user -> user.getUsername().equals(usernameOrEmail) || user.getEmail().equals(usernameOrEmail))
                .findFirst()
                .orElse(null);
    }

    public boolean authenticate(String usernameOrEmail, String password) {
        User user = findByUsernameOrEmail(usernameOrEmail);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }
} 