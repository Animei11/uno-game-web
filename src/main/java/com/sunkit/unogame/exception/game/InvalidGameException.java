package com.sunkit.unogame.exception.game;

public class InvalidGameException extends Exception{
    public final String message;

    public InvalidGameException(String message) {
        super(message);
        this.message = message;
    }
}
