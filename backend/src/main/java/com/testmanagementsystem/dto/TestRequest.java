package com.testmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestRequest {
    private String name;
    private List<QuestionRequest> questions;
}
