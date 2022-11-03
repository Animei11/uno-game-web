package com.sunkit.unogame.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Data
@Builder
public class TestGameDTO {

    private String gameId;
    private String hostToken;
    private List<Player> players;
    private Stack<Card> dealDeque;
    private Stack<Card> discardPile;
    private Player currentPlayer;
    private Boolean skipNext;
    private Integer nextDraws;
    private GameState gameState;

    public static TestGameDTO map(Game game) {
        List<Player> playersList = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            playersList.add(player);
        }

        return TestGameDTO.builder()
                .gameId(game.getGameId())
                .hostToken(game.getHostToken())
                .players(playersList)
                .dealDeque(game.getDealDeque())
                .discardPile(game.getDiscardPile())
                .currentPlayer(game.getCurrentPlayer())
                .skipNext(game.getSkipNext())
                .nextDraws(game.getNextDraws())
                .gameState(game.getGameState())
                .build();
    }
}
