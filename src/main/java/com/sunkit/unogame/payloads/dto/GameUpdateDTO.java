package com.sunkit.unogame.payloads.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GamePlayDirection;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.payloads.responses.MessageType;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class GameUpdateDTO {

    @Builder.Default
    private MessageType messageType = MessageType.GAME_UPDATE;
    private String gameId;
    private Card topCard;
    private String currentPlayerNickName;
    private Map<String, Integer> handSizes;
    private boolean skipNext;
    private int nextDraws;
    private GamePlayDirection isClockwise;
    private GameState gameState;

    public static GameUpdateDTO of(Game game) {

//        List<PlayerHandSizeDTO> handSizes = new ArrayList<>();
//
//        for (Player player : game.getPlayers()) {
//            handSizes.add(PlayerHandSizeDTO.builder()
//                            .nickName(player.getNickName())
//                            .handSize(player.getHand().size())
//                            .build());
//        }

        Map<String, Integer> handSizes = new HashMap<>();
        game.getPlayers().forEach(player -> {
            handSizes.put(player.getNickname(), player.getHand().size());
        });

        return GameUpdateDTO.builder()
                .gameId(game.getGameId())
                .topCard(game.getDiscardPile().peek())
                .currentPlayerNickName(game.getCurrentPlayer().getNickname())
                .handSizes(handSizes)
                .skipNext(game.getSkipNext())
                .nextDraws(game.getNextDraws())
                .isClockwise(game.getGamePlayDirection())
                .gameState(game.getGameState())
                .build();
    }
}
