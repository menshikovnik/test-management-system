package com.testmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Data
@Entity
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Test test;
    @ManyToOne
    private InviteToken inviteToken;
    private String name;
    private String surname;
    @Email
    private String email;
    private Double testResult;
}
