package com.sunkit.unogame.controller;

import com.sunkit.unogame.controller.requests.CreateGameRequest;
import com.sunkit.unogame.controller.requests.JoinRequest;
import com.sunkit.unogame.controller.requests.StartGameRequest;
import com.sunkit.unogame.dto.message.GameCreatedMessage;
import com.sunkit.unogame.dto.message.Message;
import com.sunkit.unogame.expection.InvalidHostTokenException;
import com.sunkit.unogame.expection.game.GameFinishedException;
import com.sunkit.unogame.expection.game.GameFullException;
import com.sunkit.unogame.expection.game.GameInProgressException;
import com.sunkit.unogame.expection.game.InvalidGameIdException;
import com.sunkit.unogame.expection.InvalidNickNameException;
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

    @PostMapping("/create")
    public ResponseEntity<?> createNewGame(
            @RequestBody CreateGameRequest request) {

        log.info("Create game request: {}" , request);

        GameCreatedMessage response;

        try {
            response = gameService.createGame(request.getNickName());
        } catch (InvalidGameIdException exception) {
            log.error("Error creating new game for: {}" , request, exception);
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

        } catch (InvalidGameIdException | GameFinishedException | GameInProgressException gameException) {

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
}
