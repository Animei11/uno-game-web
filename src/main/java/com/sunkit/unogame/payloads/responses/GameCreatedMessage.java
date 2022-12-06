package com.sunkit.unogame.payloads.responses;

import com.sunkit.unogame.model.GameState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameCreatedMessage {
    @Builder.Default
    MessageType messageType = MessageType.CREATE;
    String gameId;
    String hostToken;
    GameState gameState;

    public static GameCreatedMessage of(String gameId,
                                        String hostToken,
                                        GameState gameState) {
        return GameCreatedMessage.builder()
                .gameId(gameId)
                .hostToken(hostToken)
                .gameState(gameState)
                .build();
    }
}
