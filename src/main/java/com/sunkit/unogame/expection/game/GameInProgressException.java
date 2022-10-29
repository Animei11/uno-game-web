package com.sunkit.unogame.expection.game;

public class GameInProgressException extends InvalidGameException {
    public GameInProgressException(String message) {
        super(message);
    }
}
