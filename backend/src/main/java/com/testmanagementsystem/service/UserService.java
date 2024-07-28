package com.testmanagementsystem.service;

import com.testmanagementsystem.entity.User;

public interface UserService {
    User registerUser(String email, String password);
    User loginUser(String email, String password);
}
