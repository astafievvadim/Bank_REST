package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.CustomUser;

import java.util.List;

public interface UserService {

    UserDto createUser(CreateUserRequestDto dto);

    UserDto getUserById(Long userId);

    List<UserDto> getAllUsers();

    void deleteUser(Long userId);

    UserDto updateUser(Long userId, CreateUserRequestDto dto);

    UserDto toDto(CustomUser updated);
}
