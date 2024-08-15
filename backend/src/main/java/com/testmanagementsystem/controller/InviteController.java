package com.testmanagementsystem.controller;

import com.testmanagementsystem.dto.*;
import com.testmanagementsystem.entity.*;
import com.testmanagementsystem.mapper.TestMapper;
import com.testmanagementsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invite/")
public class InviteController {

    private final InviteTokenRepository inviteTokenRepository;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final PartialTestResultRepository partialTestResultRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

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

            Optional<TestResult> existingTestResult = testResultRepository.findByInviteToken(inviteToken);

            if (existingTestResult.isPresent()) {
                TestResult testResult = existingTestResult.get();
                if (testResult.getTestResult() != null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Test already completed");
                    response.put("result", testResult.getTestResult());

                    return ResponseEntity.ok(response);
                }

                List<PartialTestResult> partialResults = partialTestResultRepository.findByTestResultId(testResult);
                TestRequest testDTO = TestMapper.toTestDTO(testResult.getTest(), partialResults);

                return ResponseEntity.ok(testDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test result not found");
            }
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

            Test test = inviteToken.getTest();

            TestResult testResult = new TestResult();
            testResult.setTest(test);
            testResult.setName(testResultRequest.getName());
            testResult.setEmail(testResultRequest.getEmail());
            testResult.setSurname(testResultRequest.getSurname());
            testResult.setInviteToken(inviteToken);

            testResultRepository.save(testResult);

            TestRequest testDTO = TestMapper.toTestDTO(test);

            return ResponseEntity.ok(testDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error starting the test");
        }
    }

    @PostMapping("/partial-save/{testResultId}/question/{questionId}")
    public ResponseEntity<?> savePartialResult(
            @PathVariable Long testResultId,
            @PathVariable Long questionId,
            @RequestBody PartialTestResultRequest request) {

        try {
            Long answerId = request.getAnswer();
            InviteToken inviteToken = inviteTokenRepository.findByToken(request.getToken()).orElse(null);

            TestResult testResult = testResultRepository.findByInviteToken(inviteToken)
                    .orElseThrow(() -> new RuntimeException("Test result not found"));

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Answer answer = answerRepository.findById(answerId)
                    .orElseThrow(() -> new RuntimeException("Answer not found"));

            Optional<PartialTestResult> existingPartialResult =
                    partialTestResultRepository.findByTestResultIdAndQuestionId(testResult, question);

            PartialTestResult partialTestResult;
            if (existingPartialResult.isPresent()) {
                partialTestResult = existingPartialResult.get();
            } else {
                partialTestResult = new PartialTestResult();
                partialTestResult.setTestResultId(testResult);
                partialTestResult.setQuestionId(question);
            }
            partialTestResult.setAnswer(answer);
            partialTestResult.setCorrect(answer.getCorrect());

            partialTestResultRepository.save(partialTestResult);

            return ResponseEntity.ok("Answer saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/submit-test/{token}")
    public ResponseEntity<?> submitTest(
            @PathVariable String token,
            @RequestBody TestSubmissionRequest submissionRequest) {
        try {
            InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invite token not found"));

            TestResult testResult = testResultRepository.findByInviteToken(inviteToken)
                    .orElseThrow(() -> new RuntimeException("Test result not found"));

            List<Question> questions = testResult.getTest().getQuestions();
            int correctAnswersCount = 0;

            for (Question question : questions) {
                Answer correctAnswer = question.getAnswers().stream()
                        .filter(Answer::getCorrect)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Correct answer not found"));

                Long userAnswerId = submissionRequest.getAnswers().get(question.getId());
                if (correctAnswer.getId().equals(userAnswerId)) {
                    correctAnswersCount++;
                }
            }

            double result = (double) correctAnswersCount / questions.size() * 100;
            testResult.setTestResult(result);
            testResultRepository.save(testResult);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test already completed");
            response.put("result", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting test: " + e.getMessage());
        }
    }
}
