package com.testmanagementsystem.repository;

import com.testmanagementsystem.entity.InviteToken;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    Optional<TestResult> findByInviteToken(InviteToken token);
    Optional<TestResult> findByTest(Test test);
}
