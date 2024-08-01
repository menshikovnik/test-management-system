package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.AnswerRequest;
import com.testmanagementsystem.dto.QuestionRequest;
import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.entity.Answer;
import com.testmanagementsystem.entity.Question;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody TestRequest testRequest) {
        try {
            Test test = new Test();
            test.setName(testRequest.getName());
            for (QuestionRequest questionRequest : testRequest.getQuestions()) {
                Question question = new Question();
                question.setQuestionText(questionRequest.getText());
                question.setTest(test);

                for (AnswerRequest answerRequest : questionRequest.getAnswers()) {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerRequest.getText());
                    answer.setCorrect(answerRequest.isCorrect());
                    answer.setQuestion(question);
                    question.getAnswers().add(answer);
                }
                test.getQuestions().add(question);
            }
            System.out.println(test);
            testService.createTest(test);
            return ResponseEntity.ok("Test created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create test: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        try {
            List<Test> tests = testService.getAllTests();
            return ResponseEntity.ok(tests);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}