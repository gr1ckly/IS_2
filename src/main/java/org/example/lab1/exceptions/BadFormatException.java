package org.example.lab1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class BadFormatException extends RuntimeException {
    public BadFormatException(String message) {
        super(message);
    }
}
