package com.testmanagementsystem.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserRegistrationRequest {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is not valid")
    private String email;
    private String password;
}
