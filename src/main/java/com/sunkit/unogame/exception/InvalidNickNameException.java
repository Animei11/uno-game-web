package com.sunkit.unogame.exception;

public class InvalidNickNameException extends Exception{
    public final String message;

    public InvalidNickNameException(String message) {
        super(message);
        this.message = message;
    }
}
