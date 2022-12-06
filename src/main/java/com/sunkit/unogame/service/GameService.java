package com.sunkit.unogame.service;

import com.sunkit.unogame.payloads.responses.GameCreatedMessage;
import com.sunkit.unogame.payloads.responses.PlayerJoinMessage;
import com.sunkit.unogame.exception.InvalidHostTokenException;
import com.sunkit.unogame.exception.InvalidNickNameException;
import com.sunkit.unogame.exception.game.*;
import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;
import com.sunkit.unogame.storage.GameStorage;
import com.sunkit.unogame.util.GameUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static com.sunkit.unogame.model.GamePlayDirection.CLOCKWISE;

@Service
public class GameService {
    private final GameStorage gameStorage;

    public GameService() {
        this.gameStorage = GameStorage.getInstance();
    }

    public Game getGame(String gameId) throws Exception {
        return gameStorage.getGame(gameId);
    }


    public GameCreatedMessage createGame(String nickName) throws InvalidGameIdException {
        Game game = GameUtils.createNewGame(nickName);
        // ensure every game has an unique id
        while (gameStorage.hasGameWithId(game.getGameId())) {
            game.setGameId(UUID.randomUUID().toString());
        }

        gameStorage.addGame(game);

        return GameCreatedMessage.builder()
                .gameId(game.getGameId())
                .hostToken(game.getHostToken())
                .gameState(game.getGameState())
                .build();
    }

    public PlayerJoinMessage joinGame(String gameId, String nickName) throws InvalidGameIdException, InvalidNickNameException, GameInProgressException, GameFullException, GameFinishedException {

        Game game = gameStorage.getGame(gameId);

        // check is game is joinable
        switch (game.getGameState()) {
            case IN_PROGRESS -> throw new GameInProgressException(
                    "Game with id: " + gameId + " is already in progress");
            case FULL -> throw new GameFullException(
                    "Game with id: " + gameId + " is already full");
            case FINISHED -> throw new GameFinishedException(
                    "Game with id: " + gameId + " is already finished");

        }

        // check if nickname is valid
        if (game.nickNameTaken(nickName)) {
            throw new InvalidNickNameException(
                    "Nick name " + nickName + " has already been taken"
            );
        }

        // create new player if all checks have passed
        Player player = Player.builder()
                .nickname(nickName)
                .hand(new ArrayList<>())
                .build();

        // add new player to players
        game.getPlayers().add(player);

        if (game.getPlayers().size() >= 4) {
            game.setGameState(GameState.FULL);
        }

        return PlayerJoinMessage.of(
                gameId,
                nickName,
                game.getGameState(),
                game.getPlayers().size());
    }

    public void startGame(String gameId, String hostToken) throws InvalidHostTokenException, GameInProgressException, GameFinishedException, InvalidGameIdException, InsufficientPlayersException {
        Game game = gameStorage.getGame(gameId);

        // check if host token is valid
        if (game.getHostToken().equals(hostToken)) {

            // check if game has already started
            switch(game.getGameState()) {
                case IN_PROGRESS -> throw new GameInProgressException(
                        "Game has already started");
                case FINISHED -> throw new GameFinishedException(
                        "Game is already finished");
            }

            // check that there are two or more players in the game
            if (game.getPlayers().size() < 2) {
                throw new InsufficientPlayersException(
                        "Game with id: " + gameId + " has less than two players");
            }

            // start game if all checks passed
            // complete the rest of the setup of the game to start it
            GameUtils.initializeGame(game);
        } else {
            throw new InvalidHostTokenException("Host token provided is invalid");
        }
    }

    /**
     * Validates and performs the action needed to play a card in a game
     * @param gameId id of game to play the card in
     * @param playerNickName nickname of player playing the card
     * @param cardPlayed card that was played by player
     * @param newColor the new color selected by player who played a wild card
     *                 (this field will only have an impact on the result when
     *                 the cardPlayed is a wild card or draw four)
     * @throws InvalidGameIdException when game with id passed in doesn't exist
     * @throws InvalidNickNameException when player with nickname passed in isn't
     *                                  the current player in the game specified
     */
    public void playCard(
            String gameId,
            String playerNickName,
            Card cardPlayed,
            int newColor) throws InvalidGameIdException, InvalidNickNameException, InvalidPlayException {

        Game game = gameStorage.getGame(gameId);

        // ensure that the player is current player
        if (!playerNickName.equals(game.getCurrentPlayer().getNickname())) {
            throw new InvalidNickNameException(
                    "Player with nickname: " + playerNickName +
                    " is not the current player");
        }

        Player player = game.getCurrentPlayer();

        // ensure that the player has the card being played
        if (!player.getHand().contains(cardPlayed)) {
            throw new InvalidPlayException(
                    "PLayer with nickname: " + playerNickName +
                    " does not have the card: " + cardPlayed);
        }

        GameUtils.playCard(game, player, cardPlayed, newColor);

        // set next player to be current player
        nextPlayer(gameId);
    }

    public void nextPlayer(String gameId) throws InvalidGameIdException {
        Game game = gameStorage.getGame(gameId);

        // set next player to be current player
        if (game.getGamePlayDirection().equals(CLOCKWISE)) {
            game.setCurrentPlayer(game.getPlayerIterator().next());
        } else {
            game.setCurrentPlayer(game.getPlayerIterator().previous());
        }
    }

    public void skipCurrentPlayer(String gameId) throws InvalidGameIdException {
        Game game = gameStorage.getGame(gameId);
        nextPlayer(gameId);
        game.setSkipNext(false);
    }

    /**
     * Handles the draw card request from player and automatically sets the next
     * player as the current player if the draw was due to an action card played
     * by another player
     * @param gameId game id of the game being handled
     * @param playerNickName nickname of player drawing cards
     * @param numOfDraws number of cards to be drawn
     * @return a {@link List} of {@link Card} that was drawn by the player
     * @throws InvalidGameIdException when the game with id passed in cannot be found
     * @throws InvalidNickNameException when the game with game id passed in doesn't
     *                                  have a player with nickname passed in
     * @implNote Frontend needs to handle the case where a single card is drawn
     *              due to no cards to play to determine the player actions in case
     *              of an action card being played.
     */
    public List<Card> drawCards(
            String gameId,
            String playerNickName,
            Integer numOfDraws) throws InvalidGameIdException, InvalidNickNameException {

        Game game = gameStorage.getGame(gameId);

        // ensure that the player is current player
        if (!playerNickName.equals(game.getCurrentPlayer().getNickname())) {
            throw new InvalidNickNameException(
                    "Player with nickname: " + playerNickName +
                            " is not the current player");
        }

        Player player = game.getCurrentPlayer();
        Stack<Card> dealDeque = game.getDealDeque();

        List<Card> cardsDrawn = new ArrayList<>();

        // draw the number of cards requested and update the game state accordingly
        for (int i = 0; i < numOfDraws; i++) {
            Card cardDrawn = dealDeque.pop();
            player.getHand().add(cardDrawn);
            cardsDrawn.add(cardDrawn);
        }

        // reset draws for next player
        game.setNextDraws(0);

        // once a player draws a card, the turn is over unless it is playable
        // directly set next player if the draw wasn't due to no cards to play
        if (numOfDraws != 1) {
            nextPlayer(gameId);
        }

        return cardsDrawn;
    }
}
