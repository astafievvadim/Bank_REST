package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BlockCardRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Card card;

    @ManyToOne
    private CustomUser user;

    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;

    private String comment;

    public BlockCardRequest(Long id, Card card, CustomUser user, LocalDateTime requestedAt, RequestStatusEnum status, String comment) {
        this.id = id;
        this.card = card;
        this.user = user;
        this.requestedAt = requestedAt;
        this.status = status;
        this.comment = comment;
    }

    public BlockCardRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public RequestStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RequestStatusEnum status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
