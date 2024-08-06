package com.testmanagementsystem.service;

import com.testmanagementsystem.dto.AnswerRequest;
import com.testmanagementsystem.dto.QuestionRequest;
import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.entity.Answer;
import com.testmanagementsystem.entity.Question;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.exception.TestNotFoundException;
import com.testmanagementsystem.exception.TestServiceException;
import com.testmanagementsystem.repository.AnswerRepository;
import com.testmanagementsystem.repository.QuestionRepository;
import com.testmanagementsystem.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
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

    public void createTest(TestRequest testRequest) throws TestServiceException {
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

    public void updateTest(Long id, TestRequest updatedTest) throws TestServiceException {
        Test existingTest = findById(id);

        if (existingTest == null) {
            throw new TestNotFoundException("Test not found with id" + id);
        }

        existingTest.setName(updatedTest.getName());

        List<Question> existingQuestions = existingTest.getQuestions();

        for (QuestionRequest updatedQuestion : updatedTest.getQuestions()) {
            Question question;
            question = findQuestionById(existingQuestions, updatedQuestion.getId());
            if (question != null) {
                question.setQuestionText(updatedQuestion.getText());
            }

            assert question != null;
            List<Answer> existingAnswers = question.getAnswers();

            for (AnswerRequest updatedAnswer : updatedQuestion.getAnswers()) {
                Answer answer;
                if (updatedAnswer.getId() != null) {
                    answer = findAnswerById(existingAnswers, updatedAnswer.getId());
                    if (answer != null) {
                        answer.setAnswerText(updatedAnswer.getText());
                        answer.setCorrect(updatedAnswer.isCorrect());
                    }
                }
            }
        }

        save(existingTest);
    }

    @EntityGraph(attributePaths = {"questions.answers"})
    public Test findById(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    public void save(Test test) {
        testRepository.save(test);
    }

    private Question findQuestionById(List<Question> questions, Long id) {
        return questions.stream().filter(question -> question.getId().equals(id)).findFirst().orElse(null);
    }

    private Answer findAnswerById(List<Answer> answers, Long id) {
        return answers.stream().filter(answer -> answer.getId().equals(id)).findFirst().orElse(null);
    }
}