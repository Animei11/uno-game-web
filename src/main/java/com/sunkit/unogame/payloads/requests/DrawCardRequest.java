package com.sunkit.unogame.payloads.requests;

import lombok.Data;

@Data
public class DrawCardRequest {
    String gameId;
    String playerNickname;
    Integer numOfDraws;
}
