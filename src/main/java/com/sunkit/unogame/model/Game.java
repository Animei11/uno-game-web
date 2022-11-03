package com.sunkit.unogame.model;


import com.sunkit.unogame.datastructures.CircularLinkedList;
import com.sunkit.unogame.datastructures.interfaces.LinkedListIterator;
import lombok.Builder;
import lombok.Data;

import java.util.Stack;

@Data
@Builder
public class Game {
    private String gameId;
    private String hostToken;
    private CircularLinkedList<Player> players;
    private LinkedListIterator<Player> playerIterator;
    private Stack<Card> dealDeque;
    @Builder.Default
    private Stack<Card> discardPile = new Stack<>();
    private Player currentPlayer;
    @Builder.Default
    private Boolean skipNext = false;
    @Builder.Default
    private Integer nextDraws = 0;
    @Builder.Default
    private GamePlayDirection gamePlayDirection = GamePlayDirection.CLOCKWISE;
    @Builder.Default
    private GameState gameState = GameState.NEW;

    public boolean nickNameTaken(String nickName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getNickName().equals(nickName))
                return true;
        }
        return false;
    }
}
