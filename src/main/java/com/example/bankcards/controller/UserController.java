package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.repository.CustomUserRepo;
import com.example.bankcards.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CardService cardService;
    private final CustomUserRepo userRepo;

    @Autowired
    public UserController(CardService cardService, CustomUserRepo userRepo) {
        this.cardService = cardService;
        this.userRepo = userRepo;
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<CardDto>> getAllUserCards(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "expirationDate") String sortBy,
            Principal principal
    ) {
        enforceAuthorization(userId, principal);
        return ResponseEntity.ok(cardService.getCardsByUser(userId, page, size, sortBy));
    }

    @GetMapping("/{userId}/cards/{cardId}/balance")
    public ResponseEntity<Double> getCardBalance(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            Principal principal
    ) {
        enforceAuthorization(userId, principal);
        return ResponseEntity.ok(cardService.getCardById(cardId).getBalance());
    }

    @PostMapping("/{userId}/cards/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long userId,
            BlockCardRequestDto dto,
            Principal principal
    ) {
        enforceAuthorization(userId, principal);
        cardService.requestCardBlock(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/cards/transfer")
    public ResponseEntity<Void> transfer(
            @PathVariable Long userId,
            @RequestBody TransferRequestDto dto,
            Principal principal
    ) {
        enforceAuthorization(userId, principal);
        cardService.transfer(dto);
        return ResponseEntity.ok().build();
    }

    private void enforceAuthorization(Long userId, Principal principal) {
        CustomUser currentUser = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!currentUser.getId().equals(userId) && currentUser.getRole() != RoleEnum.ADMIN) {
            throw new RuntimeException("Access denied");
        }
    }
}
