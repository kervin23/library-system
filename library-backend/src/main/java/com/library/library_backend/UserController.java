package com.library.library_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<?> promote(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setRole("manager");
        userRepository.save(user);
        return ResponseEntity.ok("Promoted to manager");
    }

    @PutMapping("/{id}/demote")
    public ResponseEntity<?> demote(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        user.setRole("user");
        userRepository.save(user);
        return ResponseEntity.ok("Demoted to user");
    }
}