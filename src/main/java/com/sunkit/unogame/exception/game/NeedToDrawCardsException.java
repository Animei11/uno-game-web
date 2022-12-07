package com.sunkit.unogame.exception.game;

public class NeedToDrawCardsException extends Exception{
    private final Integer numOfCardsToDraw;
    public NeedToDrawCardsException(int numOfCardsToDraw) {
        super();
        this.numOfCardsToDraw = numOfCardsToDraw;
    }

    public Integer getNumOfCardsToDraw() {
        return numOfCardsToDraw;
    }
}
