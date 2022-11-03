package com.sunkit.unogame.controller;

import com.sunkit.unogame.controller.requests.*;
import com.sunkit.unogame.dto.CardsDrawnDTO;
import com.sunkit.unogame.dto.TestGameDTO;
import com.sunkit.unogame.dto.message.GameCreatedMessage;
import com.sunkit.unogame.dto.message.Message;
import com.sunkit.unogame.exception.InvalidHostTokenException;
import com.sunkit.unogame.exception.game.*;
import com.sunkit.unogame.exception.InvalidNickNameException;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/unoGame")
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
            response = gameService.createGame(request.getNickName());
        } catch (InvalidGameIdException exception) {
            log.error("Error creating new game for: {}", request, exception);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(exception.getMessage()));
        }

        log.info("Successfully created new game for: {}",
                request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(
            @RequestBody JoinRequest request) {

        log.info("Join game request: {}", request);

        Message response;
        try {
            response = gameService.joinGame(request.getGameId(), request.getNickName());
        } catch (InvalidGameIdException | InvalidNickNameException | GameInProgressException | GameFullException |
                 GameFinishedException exception) {
            log.error("Error processing request: {}", request, exception);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(exception.getMessage()));
        }

        // todo: notify game host to start the game if gameState is FULL

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(
            @RequestBody StartGameRequest request) {

        try {

            gameService.startGame(request.getGameId(), request.getHostToken());

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
        return ResponseEntity.ok(Message.of("Game started"));
    }

    @PostMapping("/playCard")
    public ResponseEntity<?> playCard(
            @RequestBody PlayCardRequest request) {

        log.info("Play card request: {}", request);

        try {
            gameService.playCard(
                    request.getGameId(),
                    request.getPlayerNickName(),
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
    @PostMapping("/resetSkip/{gameId}")
    public ResponseEntity<?> skipNextPlayer(
            @PathVariable("gameId") String gameId) {

        log.info("Reset skip request for game with id: {}", gameId);

        try {
            gameService.skipNextPlayer(gameId);
        } catch (InvalidGameIdException e) {
            log.error("Error resetting skip status for game with id: {}", gameId, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }
        return ResponseEntity.ok(Message.of(
                "Skip status of game with id: " + gameId +
                        " has been reset"));
    }

    @PostMapping("/draw")
    public ResponseEntity<?> drawCards(
            @RequestBody DrawCardRequest request) {

        log.info("Draw card request: {}" , request);
        CardsDrawnDTO cardsDrawnDTO;
        try {
            cardsDrawnDTO = gameService.drawCards(
                    request.getGameId(),
                    request.getPlayerNickName(),
                    request.getNumOfDraws());
        } catch (InvalidNickNameException | InvalidGameIdException e) {
            log.error("Error processing draw card request: {}", request, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Message.of(e.getMessage()));
        }

        return ResponseEntity.ok(cardsDrawnDTO);
    }
}
