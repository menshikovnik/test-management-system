package com.testmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    private String text;
    private List<AnswerRequest> answers;
    private String correctAnswer;
}