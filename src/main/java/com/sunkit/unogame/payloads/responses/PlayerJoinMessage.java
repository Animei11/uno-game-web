package com.sunkit.unogame.payloads.responses;

import com.sunkit.unogame.model.GameState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerJoinMessage {
    @Builder.Default
    MessageType messageType = MessageType.JOIN;
    String gameId;
    String playerNickname;
    GameState gameState;
    Integer numOfPlayers;

    public static PlayerJoinMessage of(String gameId,
                                       String nickname,
                                       GameState gameState,
                                       Integer numOfPlayers) {
        return PlayerJoinMessage.builder()
                .gameId(gameId)
                .playerNickname(nickname)
                .gameState(gameState)
                .numOfPlayers(numOfPlayers)
                .build();
    }
}
