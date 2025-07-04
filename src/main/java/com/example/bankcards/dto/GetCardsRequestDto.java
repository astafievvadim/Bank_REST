package com.example.bankcards.dto;

public class GetCardsRequestDto {

    private Long userId;

    private int page;

    private int size;

    private String sortBy;

    public GetCardsRequestDto(Long userId, int page, int size, String sortBy) {
        this.userId = userId;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
    }

    public GetCardsRequestDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
