package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatusEnum;

import java.time.LocalDate;

public class CardDto {

    private Long id;
    private String maskedUuid;
    private LocalDate expirationDate;
    private CardStatusEnum cardStatusEnum;
    private Double balance;

    public CardDto(Long id, String maskedUuid, LocalDate expirationDate, CardStatusEnum cardStatusEnum, Double balance) {
        this.id = id;
        this.maskedUuid = maskedUuid;
        this.expirationDate = expirationDate;
        this.cardStatusEnum = cardStatusEnum;
        this.balance = balance;
    }

    public CardDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaskedUuid() {
        return maskedUuid;
    }

    public void setMaskedUuid(String maskedUuid) {
        this.maskedUuid = maskedUuid;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CardStatusEnum getStatus() {
        return cardStatusEnum;
    }

    public void setStatus(CardStatusEnum cardStatusEnum) {
        this.cardStatusEnum = cardStatusEnum;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
