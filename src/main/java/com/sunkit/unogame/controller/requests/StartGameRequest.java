package com.sunkit.unogame.controller.requests;

import lombok.Data;

@Data
public class StartGameRequest {
    private String gameId;
    private String hostToken;
}
