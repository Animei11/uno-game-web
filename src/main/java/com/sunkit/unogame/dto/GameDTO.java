package com.sunkit.unogame.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.GameState;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameDTO {
    private String gameId;
    private List<Card> cardsToAdd;
    private List<Card> cardsToRemove;
    private Card topCard;
    private String currentPlayerNickName;
    private List<Integer> otherPlayerCardNums;
    private GameState gameState;
}
