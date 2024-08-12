package com.testmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

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
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers;
    private String name;
    private String surname;
    @Email
    private String email;
    private Double result;
}
