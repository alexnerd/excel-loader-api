package com.alexnerd.excelloader.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(String errorMsg, HttpStatus status){
        super(errorMsg);
        this.status = status;
    }
}