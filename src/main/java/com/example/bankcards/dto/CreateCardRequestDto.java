package com.example.bankcards.dto;

public class CreateCardRequestDto {

    private Long userId;

    public CreateCardRequestDto() {
    }

    public CreateCardRequestDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
