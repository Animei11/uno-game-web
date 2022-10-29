package com.sunkit.unogame.controller;

import com.sunkit.unogame.datastructures.CircularLinkedList;
import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;
import com.sunkit.unogame.service.GameService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@RestController
@CrossOrigin
@Slf4j
@AllArgsConstructor
@RequestMapping("unoGame/test")
public class TestController {

    private final GameService gameService;

    @GetMapping("/game/{gameId}")
    public TestGameDTO getGame(
            @PathVariable String gameId) throws Exception {
        return TestGameDTO.of(gameService.getGame(gameId));
    }


    @Data
    @Builder
    static class TestGameDTO {
        private String gameId;
        private String hostToken;
        private List<Player> players;
        private List<Card> dealDeque;
        private Stack<Card> discardPile;
        private Player currentPlayer;
        private Boolean skipNext;
        private Integer nextDraws;
        private GameState gameState;

        public static TestGameDTO of(Game game) {
            CircularLinkedList<Player> players = game.getPlayers();
            List<Player> playersList = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                playersList.add(players.get(i));
            }
            return new TestGameDTO(
                    game.getGameId(),
                    game.getHostToken(),
                    playersList,
                    game.getDealDeque(),
                    game.getDiscardPile(),
                    game.getCurrentPlayer(),
                    game.getSkipNext(),
                    game.getNextDraws(),
                    game.getGameState()
            );
        }
    }
}
