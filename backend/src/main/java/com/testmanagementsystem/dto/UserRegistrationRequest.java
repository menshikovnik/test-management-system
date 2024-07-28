package com.testmanagementsystem.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String email;
    private String password;
}
