package com.sunkit.unogame.payloads.requests;

import com.sunkit.unogame.model.Card;
import lombok.Data;

@Data
public class PlayCardRequest {

    private String gameId;
    private String playerNickname;
    private Card cardPlayed;
    private int newColor;
}
