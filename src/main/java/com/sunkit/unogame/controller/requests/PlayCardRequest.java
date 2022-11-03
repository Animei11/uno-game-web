package com.sunkit.unogame.controller.requests;

import com.sunkit.unogame.model.Card;
import lombok.Data;

@Data
public class PlayCardRequest {

    private String gameId;
    private String playerNickName;
    private Card cardPlayed;
    private int newColor;
}
