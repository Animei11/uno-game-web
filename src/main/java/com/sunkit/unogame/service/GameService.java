package com.sunkit.unogame.service;

import com.sunkit.unogame.dto.message.GameCreatedMessage;
import com.sunkit.unogame.dto.message.Message;
import com.sunkit.unogame.expection.InvalidHostTokenException;
import com.sunkit.unogame.expection.game.*;
import com.sunkit.unogame.expection.InvalidNickNameException;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.GameState;
import com.sunkit.unogame.model.Player;
import com.sunkit.unogame.storage.GameStorage;
import com.sunkit.unogame.util.GameUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

import static com.sunkit.unogame.model.GameState.FINISHED;
import static com.sunkit.unogame.model.GameState.IN_PROGRESS;

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
        Game game = GameUtil.createNewGame(nickName);
        // ensure every game has an unique id
        while (gameStorage.hasGameWithId(game.getGameId())) {
            game.setGameId(UUID.randomUUID().toString());
        }

        gameStorage.addGame(game);

        return GameCreatedMessage.builder()
                .gameId(game.getGameId())
                .hostToken(game.getHostToken())
                .build();
    }

    public Message joinGame(String gameId, String nickName) throws InvalidGameIdException, InvalidNickNameException, GameInProgressException, GameFullException, GameFinishedException {

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
                .nickName(nickName)
                .hand(new ArrayList<>())
                .build();

        // add new player to players
        game.getPlayers().add(player);

        if (game.getPlayers().size() >= 4) {
            game.setGameState(GameState.FULL);
        }

        return Message.of("You have successfully joined game with id: " + gameId);
    }

    public void startGame(String gameId, String hostToken) throws InvalidHostTokenException, GameInProgressException, GameFinishedException, InvalidGameIdException {
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

            // start game if all checks passed
            game.setGameState(IN_PROGRESS);
        } else {
            throw new InvalidHostTokenException("Host token provided is invalid");
        }
    }
}
