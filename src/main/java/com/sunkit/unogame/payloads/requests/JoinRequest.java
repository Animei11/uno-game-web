package com.sunkit.unogame.payloads.requests;

import lombok.Data;

@Data
public class JoinRequest {

    private String gameId;
    private String nickname;
}
