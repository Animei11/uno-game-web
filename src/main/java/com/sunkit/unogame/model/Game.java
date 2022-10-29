package com.sunkit.unogame.model;


import com.sunkit.unogame.datastructures.CircularLinkedList;
import lombok.Builder;
import lombok.Data;

import java.util.Stack;

@Data
@Builder
public class Game {
    private String gameId;
    private String hostToken;
    private CircularLinkedList<Player> players;
    private Stack<Card> dealDeque;
    private Stack<Card> discardPile;
    private Player currentPlayer;
    private Boolean skipNext;
    private Integer nextDraws;
    private GameState gameState;

    public boolean nickNameTaken(String nickName) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getNickName().equals(nickName))
                return true;
        }
        return false;
    }
}
