package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.InviteTokenRequest;
import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.dto.TestResultRequest;
import com.testmanagementsystem.entity.InviteToken;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.entity.TestResult;
import com.testmanagementsystem.mapper.TestMapper;
import com.testmanagementsystem.repository.InviteTokenRepository;
import com.testmanagementsystem.repository.TestRepository;
import com.testmanagementsystem.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invite/")
public class InviteController {

    private final InviteTokenRepository inviteTokenRepository;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;

    @PostMapping("/update-expiration/{id}")
    public ResponseEntity<?> updateExpiration(@PathVariable("id") Long id, @RequestBody InviteTokenRequest inviteTokenRequest) {
        try {
            Test test = testRepository.findById(id).orElse(null);
            assert test != null;
            test.setExpirationDate(inviteTokenRequest.getExpirationDate().plusHours(3));
            List<InviteToken> tokens = inviteTokenRepository.findAllByTestId(id);

            if (tokens.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No invite tokens found for the provided test ID.");
            }

            LocalDateTime localDateTime = inviteTokenRequest.getExpirationDate();

            tokens.forEach(token -> token.setExpirationDate(localDateTime.plusHours(3)));

            inviteTokenRepository.saveAll(tokens);

            return ResponseEntity.ok().body("Expiration date updated successfully for all tokens.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update expiration date.");
        }
    }

    @GetMapping("/register/{token}")
    public ResponseEntity<?> getTestDetails(@PathVariable String token) {
        try {
            InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invite token not found"));

            if (inviteToken.getExpirationDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invite token has expired");
            }
            return ResponseEntity.ok("Token is valid.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching test details");
        }
    }

    @PostMapping("/start-test/{token}")
    public ResponseEntity<?> startTest(
            @PathVariable String token,
            @RequestBody TestResultRequest testResultRequest) {
        try {
            InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invite token not found"));

            if (inviteToken.getExpirationDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invite token has expired");
            }

            List<Test> test = new ArrayList<>();
            test.add(inviteToken.getTest());

            TestResult testResult = new TestResult();
            testResult.setTest(test.get(0));
            testResult.setName(testResultRequest.getName());
            testResult.setEmail(testResultRequest.getEmail());
            testResult.setSurname(testResultRequest.getSurname());
            testResult.setInviteToken(inviteToken);

            testResultRepository.save(testResult);

            List<TestRequest> testDTO = test.stream()
                    .map(TestMapper::toTestDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(testDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error starting the test");
        }
    }
}
