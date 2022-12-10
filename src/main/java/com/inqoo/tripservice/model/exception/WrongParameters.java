package com.inqoo.tripservice.model.exception;

public class WrongParameters extends RuntimeException{
    public WrongParameters(String message) {
        super(message);
    }


}