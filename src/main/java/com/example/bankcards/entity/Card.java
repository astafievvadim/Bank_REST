package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String encryptedUuid;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;
    private LocalDate expirationDate;
    @Enumerated(EnumType.STRING)
    private CardStatusEnum cardStatusEnum;
    private BigDecimal balance;

    public Card(Long id, String encryptedUuid, CustomUser user, LocalDate expirationDate, CardStatusEnum cardStatusEnum, BigDecimal balance) {
        this.id = id;
        this.encryptedUuid = encryptedUuid;
        this.user = user;
        this.expirationDate = expirationDate;
        this.cardStatusEnum = cardStatusEnum;
        this.balance = balance;
    }

    public Card() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEncryptedUuid() {
        return encryptedUuid;
    }

    public void setEncryptedUuid(String encryptedUuid) {
        /*
        if (!encryptedUuid.matches("\\d{4}\\d{4}\\d{4}\\d{4}")) {
            throw new IllegalArgumentException("ID must be in xxxxxxxxxxxxxxxx format");
        }
        */
        this.encryptedUuid = encryptedUuid;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser customUser) {
        this.user = customUser;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
