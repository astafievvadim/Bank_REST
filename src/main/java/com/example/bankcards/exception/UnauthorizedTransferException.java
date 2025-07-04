package com.example.bankcards.exception;

public class UnauthorizedTransferException  extends RuntimeException  {
    public UnauthorizedTransferException(String s) {
        super(s);
    }
}
