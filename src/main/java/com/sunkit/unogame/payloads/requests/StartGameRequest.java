package com.sunkit.unogame.payloads.requests;

import lombok.Data;

@Data
public class StartGameRequest {
    private String gameId;
    private String hostToken;
}
