package com.testmanagementsystem.service;

import com.testmanagementsystem.dto.auth.AuthResponse;
import com.testmanagementsystem.dto.user.UserLoginRequest;
import com.testmanagementsystem.entity.User;
import com.testmanagementsystem.entity.VerificationToken;
import com.testmanagementsystem.repository.UserRepository;
import com.testmanagementsystem.repository.VerificationTokenRepository;
import com.testmanagementsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public void registerUser(User user) {
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        String confirmationUrl = "http://localhost:8081/api/auth/confirm?token=" + token;
        emailService.sendSimpleMessage(user.getEmail(), "Confirm your email", "To confirm your email, please click here: " + confirmationUrl);
    }

    public ResponseEntity<?> loginUser(UserLoginRequest userLoginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
        final User user = userRepository.findByEmail(userLoginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Email is not verified");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userLoginRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    public ResponseEntity<?> confirmRegistration(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email confirmed successfully. You can now log in.");
    }

}
