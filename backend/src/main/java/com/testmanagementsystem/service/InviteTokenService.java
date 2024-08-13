package com.testmanagementsystem.service;

import com.testmanagementsystem.entity.InviteToken;
import com.testmanagementsystem.entity.Test;
import com.testmanagementsystem.exception.TestNotFoundException;
import com.testmanagementsystem.repository.InviteTokenRepository;
import com.testmanagementsystem.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteTokenService {

    private final InviteTokenRepository inviteTokenRepository;

    private final TestRepository testRepository;

    public InviteToken createInviteToken(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new TestNotFoundException("Test not found"));

        InviteToken inviteToken = new InviteToken();
        inviteToken.setTest(test);
        inviteToken.setToken(UUID.randomUUID().toString());

        return inviteTokenRepository.save(inviteToken);
    }
}
