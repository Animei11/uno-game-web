package com.sunkit.unogame.payloads.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GamePlayDirection;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.payloads.responses.MessageType;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class GameUpdateDTO {

    @Builder.Default
    private MessageType messageType = MessageType.GAME_UPDATE;
    private String gameId;
    private Card topCard;
    private String currentPlayerNickName;
    private Map<String, List<Card>> hands;
    private boolean skipNext;
    private int nextDraws;
    private GamePlayDirection isClockwise;
    private GameState gameState;

    public static GameUpdateDTO of(Game game) {

        Map<String, List<Card>> handSizes = new HashMap<>();
        game.getPlayers().forEach(player -> {
            handSizes.put(player.getNickname(), player.getHand());
        });

        return GameUpdateDTO.builder()
                .gameId(game.getGameId())
                .topCard(game.getDiscardPile().peek())
                .currentPlayerNickName(game.getCurrentPlayer().getNickname())
                .hands(handSizes)
                .skipNext(game.getSkipNext())
                .nextDraws(game.getNextDraws())
                .isClockwise(game.getGamePlayDirection())
                .gameState(game.getGameState())
                .build();
    }
}
