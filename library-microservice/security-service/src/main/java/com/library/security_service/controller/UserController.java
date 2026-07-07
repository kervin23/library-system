package com.library.security_service.controller;

import com.library.security_service.entity.User;
import com.library.security_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<?> promote(@PathVariable Long id) {
        User user = userService.promote(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Promoted to manager");
    }

    @PutMapping("/{id}/demote")
    public ResponseEntity<?> demote(@PathVariable Long id) {
        User user = userService.demote(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Demoted to user");
    }
}