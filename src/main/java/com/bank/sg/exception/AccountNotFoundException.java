package com.bank.sg.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account not found exception");
    }
}
