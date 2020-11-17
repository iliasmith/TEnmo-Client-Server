package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Account not found.")
public class AccountNotFound extends Exception {

    public AccountNotFound(String message) {
        super(message);
    }
}
