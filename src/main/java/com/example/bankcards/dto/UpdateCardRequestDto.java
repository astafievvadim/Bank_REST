package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatusEnum;

public class UpdateCardRequestDto {

    private Long id;
    private CardStatusEnum cardStatusEnum;

    public UpdateCardRequestDto(Long id, CardStatusEnum cardStatusEnum) {
        this.id = id;
        this.cardStatusEnum = cardStatusEnum;
    }

    public UpdateCardRequestDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CardStatusEnum getStatus() {
        return cardStatusEnum;
    }

    public void setStatus(CardStatusEnum cardStatusEnum) {
        this.cardStatusEnum = cardStatusEnum;
    }
}
