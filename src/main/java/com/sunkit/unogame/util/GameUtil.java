package com.sunkit.unogame.util;

import com.sunkit.unogame.datastructures.CircularLinkedList;
import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static com.sunkit.unogame.model.GameState.*;

public class GameUtil {
    public static Game createNewGame(String hostNickName) {

        // construct host player
        Player host = Player.builder()
                .nickName(hostNickName)
                .hand(new ArrayList<>())
                .build();
        CircularLinkedList<Player> players = new CircularLinkedList<>();
        players.add(host);

        // return new game
        return Game.builder()
                .gameId(UUID.randomUUID().toString())
                .hostToken(UUID.randomUUID().toString())
                .players(players)
                .dealDeque(generateDealDeque())
                .discardPile(new Stack<>())
                .currentPlayer(host)
                .skipNext(false)
                .nextDraws(0)
                .gameState(NEW)
                .build();
    }

    private static List<Card> generateDealDeque() {
        return null;
    }
}
