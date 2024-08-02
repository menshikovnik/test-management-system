package com.testmanagementsystem.dto;

import lombok.Data;

@Data
public class AnswerRequest {
    private Long id;
    private String text;
    private boolean correct;
}
