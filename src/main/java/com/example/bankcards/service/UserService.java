package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.entity.CustomUser;

import java.util.List;

public interface UserService {

    CustomUser createUser(CreateUserRequest dto);
    CustomUser getUserById(Long userId);
    List<CustomUser> getAllUsers();

}
