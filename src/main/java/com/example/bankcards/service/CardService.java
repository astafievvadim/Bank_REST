package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;

import java.util.List;

public interface CardService {

    CardDto createCard(CreateCardRequestDto dto);

    CardDto getCardDtoById(Long cardId);
    Card getCardById(Long cardId);

    List<CardDto> getCardsByUser(Long userId, int page, int size, String sortBy);

    List<CardDto> getAllCards();

    void updateCard(Long cardId, UpdateCardRequestDto dto);

    void deleteCardById(Long cardId);

    CardDto toDto(Card dto);

    void transfer(Long userId, TransferRequestDto dto);

    void requestCardBlock(BlockCardRequestDto dto);

}
