package com.library.security_service.service;

import com.library.security_service.entity.User;
import com.library.security_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User promote(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        user.setRole("manager");
        return userRepository.save(user);
    }

    public User demote(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return null;
        user.setRole("user");
        return userRepository.save(user);
    }
}