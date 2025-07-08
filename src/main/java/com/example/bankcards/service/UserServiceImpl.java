package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CustomUserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final CustomUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(CustomUserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(CreateUserRequestDto dto) {
        CustomUser user = toEntity(dto);
        CustomUser savedUser = userRepo.save(user);
        return toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        CustomUser user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return  toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepo.deleteById(userId);
    }

    @Override
    public UserDto updateUser(Long userId, CreateUserRequestDto dto) {
        CustomUser user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        CustomUser updatedUser = userRepo.save(user);
        return toDto(updatedUser);
    }

    private CustomUser toEntity(CreateUserRequestDto dto) {
        CustomUser user = new CustomUser();
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRegistrationDate(new Date());
        return user;
    }
    @Override
    public UserDto toDto(CustomUser user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                user.getRegistrationDate()
        );
    }
}
