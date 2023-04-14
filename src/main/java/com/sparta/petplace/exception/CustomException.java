package com.sparta.petplace.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private final Error error;

    @Override
    public String getMessage() {
        return error.getMessage();
    }
}