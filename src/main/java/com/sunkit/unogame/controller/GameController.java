package com.sunkit.unogame.controller;

import com.sunkit.unogame.exception.InvalidHostTokenException;
import com.sunkit.unogame.exception.InvalidNickNameException;
import com.sunkit.unogame.exception.game.*;
import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.payloads.dto.GameUpdateDTO;
import com.sunkit.unogame.payloads.dto.TestGameDTO;
import com.sunkit.unogame.payloads.requests.*;
import com.sunkit.unogame.payloads.responses.GameCreatedMessage;
import com.sunkit.unogame.payloads.responses.Message;
import com.sunkit.unogame.payloads.responses.PlayerJoinMessage;
import com.sunkit.unogame.payloads.responses.StartGameMessage;
import com.sunkit.unogame.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(
            @PathVariable("gameId") String gameId) {

        log.info("Getting game with id: {}", gameId);

        Game game;

        try {
            game = gameService.getGame(gameId);
        } catch (Exception e) {
            log.error("Error getting game with id: {}", gameId, e);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        return ResponseEntity.ok(TestGameDTO.map(game));

    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewGame(
            @RequestBody CreateGameRequest request) {

        log.info("Create game request: {}", request);

        GameCreatedMessage response;

        try {
            response = gameService.createGame(request.getNickname());
        } catch (InvalidGameIdException exception) {
            log.error("Error creating new game for: {}", request, exception);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(exception.getMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(
            @RequestBody JoinRequest request) {

        log.info("Join game request: {}", request);

        PlayerJoinMessage response;
        try {
            response = gameService.joinGame(request.getGameId(), request.getNickname());
        } catch (InvalidGameIdException | InvalidNickNameException | GameInProgressException | GameFullException |
                 GameFinishedException exception) {
            log.error("Error processing request: {}", request, exception);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(exception.getMessage()));
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/game-progress/" + request.getGameId(), response);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(
            @RequestBody StartGameRequest request) {

        log.info("Starting game with id: {}", request.getGameId());

        GameUpdateDTO gameUpdateDTO;
        try {

            gameUpdateDTO = gameService.startGame(request.getGameId(), request.getHostToken());

        } catch (InvalidGameIdException | GameFinishedException |
                 GameInProgressException | InsufficientPlayersException gameException) {

            log.error("Error processing request: {}", request, gameException);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(gameException.getMessage()));
        } catch (InvalidHostTokenException invalidHostTokenException) {
            log.error("Request denied: {}", request, invalidHostTokenException);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Message.of(invalidHostTokenException.getMessage()));
        }

        StartGameMessage startGameResponse = new StartGameMessage(gameUpdateDTO);
        simpMessagingTemplate.convertAndSend(
                "/topic/game-progress/" + request.getGameId(),
                startGameResponse);

        return ResponseEntity.ok(startGameResponse);
    }

    @PostMapping("/playCard")
    public ResponseEntity<?> playCard(
            @RequestBody PlayCardRequest request) {

        log.info("Play card request: {}", request);

        try {
            gameService.playCard(
                    request.getGameId(),
                    request.getPlayerNickname(),
                    request.getCardPlayed(),
                    request.getNewColor());

        } catch (Exception e) {
            log.error("Invalid play card request: {}", request, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        // todo: broadcast play to all players

        return ResponseEntity.ok(Message.of("Card played"));
    }

    // called when a player has been skipped
    @PostMapping("/skip/{gameId}")
    public ResponseEntity<?> skipCurrentPlayer(
            @PathVariable("gameId") String gameId) {

        log.info("Reset skip request for game with id: {}", gameId);

        try {
            gameService.skipCurrentPlayer(gameId);
        } catch (InvalidGameIdException e) {
            log.error("Error resetting skip status for game with id: {}", gameId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        // todo: broadcast to all other players

        return ResponseEntity.ok(Message.of(
                "Skip status of game with id: " + gameId +
                        " has been reset"));
    }

    @PostMapping("/draw")
    public ResponseEntity<?> drawCards(
            @RequestBody DrawCardRequest request) {

        log.info("Draw card request: {}" , request);
        List<Card> cardsDrawn;
        try {
            cardsDrawn = gameService.drawCards(
                    request.getGameId(),
                    request.getPlayerNickname(),
                    request.getNumOfDraws());
        } catch (InvalidNickNameException | InvalidGameIdException e) {
            log.error("Error processing draw card request: {}", request, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        return ResponseEntity.ok(cardsDrawn);
    }
}
