package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.dto.CreateUserRequestDto;
import com.example.bankcards.dto.DeclineCommentDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.exception.AccessDeniedRuntimeException;
import com.example.bankcards.repository.CustomUserRepo;
import com.example.bankcards.service.BlockCardRequestService;
import com.example.bankcards.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CustomUserRepo userRepo;
    private final BlockCardRequestService blockRequestService;

    @Autowired
    public AdminController(UserService userService, CustomUserRepo userRepo, BlockCardRequestService blockRequestService) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.blockRequestService = blockRequestService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(
            @RequestBody CreateUserRequestDto dto,
            Principal principal
    ) {
        enforceAdmin(principal);
        UserDto createdUser = userService.createUser(dto);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(Principal principal) {
        enforceAdmin(principal);
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long userId,
            Principal principal
    ) {
        enforceAdmin(principal);
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody CreateUserRequestDto dto,
            Principal principal
    ) {
        enforceAdmin(principal);
        UserDto updatedUser = userService.updateUser(userId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Principal principal
    ) {
        enforceAdmin(principal);
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    private void enforceAdmin(Principal principal) {
        CustomUser currentUser = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (currentUser.getRole() != RoleEnum.ADMIN) {
            throw new AccessDeniedRuntimeException("Access denied");
        }
    }
    @GetMapping("/block-requests")
    public ResponseEntity<List<BlockCardRequestDto>> getAllBlockRequests(Principal principal) {
        enforceAdmin(principal);
        List<BlockCardRequestDto> requests = blockRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/block-requests/{requestId}/approve")
    public ResponseEntity<BlockCardRequest> approveBlockRequest(
            @PathVariable Long requestId,
            Principal principal
    ) {
        enforceAdmin(principal);
        BlockCardRequest approved = blockRequestService.approveRequest(requestId);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/block-requests/{requestId}/decline")
    public ResponseEntity<BlockCardRequest> declineBlockRequest(
            @PathVariable Long requestId,
            @RequestBody(required = false) DeclineCommentDto dto,
            Principal principal
    ) {
        enforceAdmin(principal);
        String comment = dto != null ? dto.getComment() : null;
        BlockCardRequest declined = blockRequestService.declineRequest(requestId, comment);
        return ResponseEntity.ok(declined);
    }
}
