package com.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class UserApiApplication {

    private List<User> users = new ArrayList<>();
    private int nextId = 1;

    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }

     @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setPort(5003);
    }

    // GET /users - Fetch all users
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(users);
    }

    // GET /users/{userId} - Fetch user by ID
    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(@PathVariable int userId) {
        Optional<User> user = users.stream().filter(u -> u.getId() == userId).findFirst();
        return user.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));

    }

    // DELETE /users - Delete all users
    @DeleteMapping("/users")
    public ResponseEntity<List<User>> deleteAllUsers() {
        users.clear();
        nextId = 1;
        return ResponseEntity.ok(users);
    }

    // POST /users - Add a new user
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUser(@RequestBody User newUser) {
        String name = newUser.getName();
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name is required and must be a non-empty string");
        }
        newUser.setId(nextId++);
        newUser.setHoursWorked(0);
        users.add(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // PUT /users/{userId} - Update user's name by ID
    @PutMapping(value = "/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody User updatedUser) {
        Optional<User> existingUser = users.stream().filter(u -> u.getId() == userId).findFirst();
        if (existingUser.isPresent()) {
            String name = updatedUser.getName();
            if (name != null && !name.trim().isEmpty()) {
                existingUser.get().setName(name.trim());
            }
            return ResponseEntity.ok(existingUser.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // PATCH /users/{userId} - Update hours worked for a user
    @PatchMapping(value = "/users/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserHours(@PathVariable int userId, @RequestBody HoursUpdate hoursUpdate) {
        Optional<User> user = users.stream().filter(u -> u.getId() == userId).findFirst();
        if (user.isPresent()) {
            if (hoursUpdate.getHoursToAdd() != null) {
                user.get().setHoursWorked(user.get().getHoursWorked() + hoursUpdate.getHoursToAdd());
                return ResponseEntity.ok(user.get());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid hoursToAdd value");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // DELETE /users/{userId} - Delete a user by ID
    @DeleteMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        Optional<User> userToDelete = users.stream().filter(u -> u.getId() == userId).findFirst();
        if (userToDelete.isPresent()) {
            users.remove(userToDelete.get());
            return ResponseEntity.ok(userToDelete.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}

// User class
class User {
    private int id;
    private String name;
    private int hoursWorked;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(int hoursWorked) { this.hoursWorked = hoursWorked; }
}

// HoursUpdate class for PATCH /users/{userId}
class HoursUpdate {
    private Integer hoursToAdd;

    // Getters and Setters
    public Integer getHoursToAdd() { return hoursToAdd; }
    public void setHoursToAdd(Integer hoursToAdd) { this.hoursToAdd = hoursToAdd; }
}
