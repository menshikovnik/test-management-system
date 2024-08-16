package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.user.UserLoginRequest;
import com.testmanagementsystem.dto.user.UserRegistrationRequest;
import com.testmanagementsystem.entity.User;
import com.testmanagementsystem.repository.UserRepository;
import com.testmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Validated
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.findByEmail(userRegistrationRequest.getEmail()) != null) {
            throw new RuntimeException("User already exists");
        }
        User user = new User(userRegistrationRequest.getEmail(), passwordEncoder.encode(userRegistrationRequest.getPassword()));
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        try {
            return userService.loginUser(userLoginRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(String token) {
        try {
            return userService.confirmRegistration(token);
        } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()); //TODO
        return ResponseEntity.ok(user);
    }
}