package com.sunkit.unogame.expection.game;

public class InvalidGameException extends Exception{
    public final String message;

    public InvalidGameException(String message) {
        super(message);
        this.message = message;
    }
}
