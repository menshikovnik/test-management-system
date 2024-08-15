package com.testmanagementsystem.dto;

import lombok.Data;

@Data
public class PartialTestResultRequest {
    private Long answer;
    private String token;
}
