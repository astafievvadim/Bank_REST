package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CustomUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private CustomUserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldReturnCreatedUserDto() {
        // Arrange
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setName("Alice");
        dto.setLastname("Smith");
        dto.setEmail("alice@example.com");
        dto.setPassword("password");
        dto.setBirthDate(new Date());
        dto.setRole(RoleEnum.USER);

        CustomUser savedUser = new CustomUser();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setLastname(dto.getLastname());
        savedUser.setEmail(dto.getEmail());
        savedUser.setRole(dto.getRole());
        savedUser.setRegistrationDate(new Date());

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");
        when(userRepo.save(any(CustomUser.class))).thenReturn(savedUser);

        // Act
        UserDto result = userService.createUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        verify(userRepo).save(any(CustomUser.class));
    }

    @Test
    void getUserById_shouldReturnUserDto() {
        CustomUser user = new CustomUser();
        user.setId(1L);
        user.setName("Bob");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bob", result.getName());
    }

    @Test
    void getUserById_whenNotFound_shouldThrowException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_shouldReturnUserDtoList() {
        List<CustomUser> users = List.of(
                new CustomUser(1L, "A", "X", new Date(),RoleEnum.USER, "a@example.com",  null, null, null),
                new CustomUser(2L, "B", "Y", new Date(),RoleEnum.USER, "b@example.com", null, null, null)
        );

        when(userRepo.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void deleteUser_shouldCallRepoDelete() {
        when(userRepo.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepo).deleteById(1L);
    }

    @Test
    void deleteUser_whenNotFound_shouldThrowException() {
        when(userRepo.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void updateUser_shouldReturnUpdatedDto() {
        CreateUserRequestDto dto = new CreateUserRequestDto();
        dto.setName("New");
        dto.setLastname("Name");
        dto.setEmail("new@example.com");
        dto.setBirthDate(new Date());
        dto.setPassword("newpass");
        dto.setRole(RoleEnum.ADMIN);

        CustomUser user = new CustomUser();
        user.setId(1L);
        user.setPassword("oldpass");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-newpass");
        when(userRepo.save(any(CustomUser.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, dto);

        assertNotNull(result);
        verify(userRepo).save(user);
        assertEquals("New", user.getName());
        assertEquals("encoded-newpass", user.getPassword());
    }
}
