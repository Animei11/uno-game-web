package com.sunkit.unogame.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class GameDTO {
    private String gameId;
    private Card topCard;
    private String currentPlayerNickName;
    private List<PlayerHandSizeDTO> handSizes;
    private GameState gameState;

    public static GameDTO of(Game game) {

        List<PlayerHandSizeDTO> handSizes = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            handSizes.add(PlayerHandSizeDTO.builder()
                            .nickName(player.getNickName())
                            .handSize(player.getHand().size())
                            .build());
        }

        return GameDTO.builder()
                .gameId(game.getGameId())
                .topCard(game.getDiscardPile().peek())
                .currentPlayerNickName(game.getCurrentPlayer().getNickName())
                .handSizes(handSizes)
                .gameState(game.getGameState())
                .build();
    }
}
