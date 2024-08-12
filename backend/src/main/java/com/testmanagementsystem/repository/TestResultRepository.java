package com.testmanagementsystem.repository;

import com.testmanagementsystem.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
}
