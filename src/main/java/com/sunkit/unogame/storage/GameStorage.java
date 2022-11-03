package com.sunkit.unogame.storage;

import com.sunkit.unogame.exception.game.InvalidGameIdException;
import com.sunkit.unogame.model.Game;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameStorage {

    private static GameStorage instance;

    private final Map<String, Game> games;

    private GameStorage() {
        games = new ConcurrentHashMap<>();
    }

    public List<Game> getGames() {
        return games.values().stream()
                .toList();
    }

    public Game getGame(String gameId) throws InvalidGameIdException {
        if (!games.containsKey(gameId)) {
            throw new InvalidGameIdException(
                    "Game with id: " + gameId + " doesn't exist"
            );
        }
        return games.get(gameId);
    }

    public void addGame(Game game) throws InvalidGameIdException {
        if (games.containsKey(game.getGameId())) {
            throw new InvalidGameIdException(
                    "Game with id: " + game.getGameId() + " already exists"
            );
        }

        games.put(game.getGameId(), game);
    }

    public void removeGame(String gameId) throws Exception {
        if (!games.containsKey(gameId)) {
            throw new Exception();
        }

        games.remove(gameId);
    }

    public boolean hasGameWithId(String gameId) {
        return games.containsKey(gameId);
    }

    public static GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }

        return instance;
    }
}
