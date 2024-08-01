package com.testmanagementsystem.service;

import com.testmanagementsystem.entity.Answer;
import com.testmanagementsystem.entity.Question;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.repository.AnswerRepository;
import com.testmanagementsystem.repository.QuestionRepository;
import com.testmanagementsystem.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Autowired
    public TestService(TestRepository testRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public void createTest(Test test) {
        Test savedTest = testRepository.save(test);
        for (Question question : test.getQuestions()) {
            question.setTest(savedTest);
            Question savedQuestion = questionRepository.save(question);
            for (Answer answer : question.getAnswers()) {
                answer.setQuestion(savedQuestion);
                answerRepository.save(answer);
            }
        }
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }
}