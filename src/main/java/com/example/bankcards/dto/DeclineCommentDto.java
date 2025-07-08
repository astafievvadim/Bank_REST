package com.example.bankcards.dto;


public class DeclineCommentDto {
    private String comment;

    public DeclineCommentDto(String comment) {
        this.comment = comment;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
