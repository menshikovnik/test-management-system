package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.entity.InviteToken;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.entity.User;
import com.testmanagementsystem.exception.TestNotFoundException;
import com.testmanagementsystem.exception.TestServiceException;
import com.testmanagementsystem.mapper.TestMapper;
import com.testmanagementsystem.repository.InviteTokenRepository;
import com.testmanagementsystem.repository.TestRepository;
import com.testmanagementsystem.repository.UserRepository;
import com.testmanagementsystem.service.InviteTokenService;
import com.testmanagementsystem.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final InviteTokenService inviteTokenService;
    private final InviteTokenRepository inviteTokenRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody TestRequest testRequest) {
        try {
            testService.createTest(testRequest);
            return ResponseEntity.ok("Test created successfully");
        } catch (TestServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<TestRequest>> getAllTests(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName());
        List<Test> tests = testRepository.findByUserId(user.getId());

        List<TestRequest> testDTOs = tests.stream()
                .map(TestMapper::toTestDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(testDTOs);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTest(@PathVariable("id") Long id, @RequestBody TestRequest updatedTest) {
        try {
            testService.updateTest(id, updatedTest);
            return ResponseEntity.ok().body("Test updated successfully");
        } catch (TestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TestServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTest(@PathVariable Long id) {
        try {
            testService.deleteTest(testService.findById(id));
            return ResponseEntity.ok().body("Test deleted successfully");
        } catch (TestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/generate-invite-link/{id}")
    public ResponseEntity<?> generateInviteLink(@PathVariable("id") Long id) {
        try {
            Test test = testRepository.findById(id).orElse(null);
            assert test != null;
            LocalDateTime localDateTime = test.getExpirationDate();
            InviteToken inviteToken = inviteTokenService.createInviteToken(id);
            inviteToken.setExpirationDate(localDateTime);
            inviteTokenRepository.save(inviteToken);
            String link = String.format("%s/invite/register/%s", getBaseUrl(), inviteToken.getToken());
            return ResponseEntity.ok().body(Collections.singletonMap("inviteLink", link));
        } catch (TestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate invite link");
        }
    }

    private String getBaseUrl() {
        return "http://localhost:3000";
    }
}