package com.testmanagementsystem.mapper;

import com.testmanagementsystem.dto.AnswerRequest;
import com.testmanagementsystem.dto.QuestionRequest;
import com.testmanagementsystem.dto.TestRequest;
import com.testmanagementsystem.entity.Answer;
import com.testmanagementsystem.entity.Question;
import com.testmanagementsystem.entity.Test;

import java.util.stream.Collectors;

public class TestMapper {

    public static TestRequest toTestDTO(Test test) {
        TestRequest testDTO = new TestRequest();
        testDTO.setId(test.getId());
        testDTO.setName(test.getName());
        testDTO.setQuestions(test.getQuestions().stream()
                .map(TestMapper::toQuestionDTO)
                .collect(Collectors.toList()));
        return testDTO;
    }

    public static QuestionRequest toQuestionDTO(Question question) {
        QuestionRequest questionDTO = new QuestionRequest();
        questionDTO.setId(question.getId());
        questionDTO.setText(question.getQuestionText());
        questionDTO.setAnswers(question.getAnswers().stream()
                .map(TestMapper::toAnswerDTO)
                .collect(Collectors.toList()));
        return questionDTO;
    }

    public static AnswerRequest toAnswerDTO(Answer answer) {
        AnswerRequest answerDTO = new AnswerRequest();
        answerDTO.setId(answer.getId());
        answerDTO.setText(answer.getAnswerText());
        answerDTO.setCorrect(answer.isCorrect());
        return answerDTO;
    }
}
