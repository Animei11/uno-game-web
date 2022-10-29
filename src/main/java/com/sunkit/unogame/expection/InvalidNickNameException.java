package com.sunkit.unogame.expection;

public class InvalidNickNameException extends Exception{
    public final String message;

    public InvalidNickNameException(String message) {
        super(message);
        this.message = message;
    }
}
