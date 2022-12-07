package com.sunkit.unogame.payloads.responses;

import lombok.Data;

@Data
public class DrawCardMessage {
    MessageType messageType = MessageType.DRAW_CARD;
    Integer numOfCards;
    public DrawCardMessage(int numOfCards) {
        this.numOfCards = numOfCards;
    }

    public static DrawCardMessage of(int numOfCards) {
        return new DrawCardMessage(numOfCards);
    }
}
