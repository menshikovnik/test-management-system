package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.invite.InviteTokenRequest;
import com.testmanagementsystem.dto.test.TestRequest;
import com.testmanagementsystem.exception.TestNotFoundException;
import com.testmanagementsystem.exception.TestServiceException;
import com.testmanagementsystem.service.InviteTokenService;
import com.testmanagementsystem.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;
    private final InviteTokenService inviteTokenService;

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
        return ResponseEntity.ok(testService.getAllTestForCurrentUser(authentication));
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
            return ResponseEntity.ok().body(Collections.singletonMap("inviteLink", testService.generateInviteLink(id)));
        } catch (TestNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate invite link");
        }
    }

    @GetMapping("/results/{userId}/")
    public ResponseEntity<?> getAllTestResultsByUser(@PathVariable Long userId) {
        try {
            return testService.getTestResult(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/update-expiration/{id}")
    public ResponseEntity<?> updateExpiration(@PathVariable("id") Long id, @RequestBody InviteTokenRequest inviteTokenRequest) {
        try {
            return inviteTokenService.updateExpiration(id, inviteTokenRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update expiration date.");
        }
    }
}