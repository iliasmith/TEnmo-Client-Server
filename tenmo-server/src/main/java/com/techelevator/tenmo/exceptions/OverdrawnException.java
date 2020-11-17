package com.techelevator.tenmo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED, reason = "Amount is greater than current account balance.")
public class OverdrawnException extends Exception {

    public OverdrawnException(String message) {
        super(message);
    }
}
