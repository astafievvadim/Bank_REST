package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockCardRequestDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
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
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final CustomUserRepo userRepo;

    @Autowired
    public CardController(CardService cardService, CustomUserRepo userRepo) {
        this.cardService = cardService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<List<CardDto>> getAllUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "expirationDate") String sortBy,
            Principal principal
    ) {
        Long userId = getUserIdFromPrincipal(principal);
        return ResponseEntity.ok(cardService.getCardsByUser(userId, page, size, sortBy));
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<Double> getCardBalance(
            @PathVariable Long cardId,
            Principal principal
    ) {
        Long userId = getUserIdFromPrincipal(principal);
        enforceCardOwnership(cardId, userId);
        return ResponseEntity.ok(cardService.getCardDtoById(cardId).getBalance());
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long cardId,
            @RequestBody BlockCardRequestDto dto,
            Principal principal
    ) {
        Long userId = getUserIdFromPrincipal(principal);
        enforceCardOwnership(cardId, userId);
        cardService.requestCardBlock(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @RequestBody TransferRequestDto dto,
            Principal principal
    ) {
        Long userId = getUserIdFromPrincipal(principal);

        cardService.transfer(userId, dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/users/{userId}/cards")
    public ResponseEntity<List<CardDto>> getCardsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "expirationDate") String sortBy,
            Principal principal
    ) {
        enforceAdmin(principal);
        return ResponseEntity.ok(cardService.getCardsByUser(userId, page, size, sortBy));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }

    private void enforceAdmin(Principal principal) {
        CustomUser currentUser = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (currentUser.getRole() != RoleEnum.ADMIN) {
            throw new RuntimeException("Access denied");
        }
    }

    private void enforceCardOwnership(Long cardId, Long userId) {
        Card card = cardService.getCardById(cardId);
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied to this card");
        }
    }
}