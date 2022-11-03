package com.sunkit.unogame.controller.requests;

import lombok.Data;

@Data
public class DrawCardRequest {
    String gameId;
    String playerNickName;
    Integer numOfDraws;
}
