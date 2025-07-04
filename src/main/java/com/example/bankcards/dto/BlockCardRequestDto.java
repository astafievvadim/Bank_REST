package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CustomUser;
import com.example.bankcards.entity.RequestStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class BlockCardRequestDto {

    private Long cardId;

    private Long userId;
    private String comment;

    public BlockCardRequestDto(Long cardId, Long userId, OffsetDateTime  requestedAt, String comment) {
        this.cardId = cardId;
        this.userId = userId;
        this.comment = comment;
    }

    public BlockCardRequestDto() {
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
