package com.sunkit.unogame.payloads.responses;

import lombok.Data;

@Data
public class CardsDrawnMessage {
    MessageType messageType = MessageType.CARDS_DRAWN;
    String playerNickname;
    Integer numOfCardsDrawn;

    public CardsDrawnMessage(String playerNickname,
                             Integer numOfCardsDrawn) {
        this.playerNickname = playerNickname;
        this.numOfCardsDrawn = numOfCardsDrawn;
    }
}
