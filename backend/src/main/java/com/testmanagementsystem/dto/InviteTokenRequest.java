package com.testmanagementsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InviteTokenRequest {
    private LocalDateTime expirationDate;
}
