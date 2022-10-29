package com.sunkit.unogame.controller.requests;

import com.sunkit.unogame.model.Card;
import lombok.Data;

@Data
public class GamePlayRequest {

    private String gameId;
    private String playerId;
    private Card cardPlayed;
}
