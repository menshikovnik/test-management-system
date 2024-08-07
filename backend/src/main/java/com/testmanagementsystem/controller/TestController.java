package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.exception.TestNotFoundException;
import com.testmanagementsystem.exception.TestServiceException;
import com.testmanagementsystem.mapper.TestMapper;
import com.testmanagementsystem.repository.TestRepository;
import com.testmanagementsystem.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;
    private final TestRepository testRepository;

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
    public ResponseEntity<List<TestRequest>> getAllTests() {
        List<Test> tests = testRepository.findAll();
        List<TestRequest> testDTOs = tests.stream()
                .map(TestMapper::toTestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(testDTOs);
    }

    @PutMapping("/get/{id}")
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
}