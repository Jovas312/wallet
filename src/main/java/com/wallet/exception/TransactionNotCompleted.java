package com.wallet.exception;

public class TransactionNotCompleted extends RuntimeException {
    public TransactionNotCompleted(String message) {
        super(message);
    }
}
