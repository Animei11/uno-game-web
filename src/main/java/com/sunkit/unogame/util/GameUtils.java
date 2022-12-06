package com.sunkit.unogame.util;

import com.sunkit.unogame.datastructures.CircularLinkedList;
import com.sunkit.unogame.exception.game.InvalidPlayException;
import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.model.Game;
import com.sunkit.unogame.model.Player;
import org.springframework.lang.NonNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static com.sunkit.unogame.model.GamePlayDirection.CLOCKWISE;
import static com.sunkit.unogame.model.GamePlayDirection.COUNTER_CLOCKWISE;
import static com.sunkit.unogame.model.GameState.IN_PROGRESS;

public class GameUtils {
    public static Game createNewGame(String hostNickName) {

        // construct host player
        Player host = Player.builder()
                .nickname(hostNickName)
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
                .currentPlayer(host)
                .build();
    }

    private static Stack<Card> generateDealDeque() {
        Stack<Card> stack = new Stack<>();
        List<Card> cards = new ArrayList<>();
        // cards not wild
        for (int n = 0; n < 2; n++) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 13; j++) {
                    if (n > 0 && j == 0) {
                        continue;
                    }
                    cards.add(new Card(j, i));
                }
            }
        }
        // wild cards
        for (int i = 0; i < 8; i++) {
            if (i < 4) {
                cards.add(new Card(13, 4));
            } else {
                cards.add(new Card(14, 4));
            }
        }

        shuffleCards(cards);

        cards.forEach(stack::push);
        return stack;
    }

    private static void shuffleCards(List<Card> cards) {

        SecureRandom random = new SecureRandom();
        int size = cards.size();
        for (int i = 0; i < 10000; i++) {
            int rng1 = random.nextInt(size);
            int rng2 = random.nextInt(size);
            Card card1 = cards.get(rng1);
            Card card2 = cards.get(rng2);
            cards.set(rng1, card2);
            cards.set(rng2, card1);
        }
    }


    public static void initializeGame(Game game) {
        CircularLinkedList<Player> players = game.getPlayers();
        Stack<Card> dealDeque = game.getDealDeque();

        // deal 7 cards to each player
        int HAND_SIZE = 7;
        for (int i = 0; i < HAND_SIZE; i++) {
            for (Player player : players) {
                player.getHand().add(dealDeque.pop());
            }
        }

        // add first card to discard pile
        Stack<Card> discardPile = game.getDiscardPile();
        Card firstCard = null;
        List<Card> invalidFirstCards = new ArrayList<>();
        // ensure that it isn't an action card
        while (firstCard == null || isActionCard(firstCard)) {
            if (firstCard != null) {
                invalidFirstCards.add(firstCard);
            }
            firstCard = dealDeque.pop();
        }
        discardPile.push(firstCard);

        invalidFirstCards.forEach(dealDeque::push);
        shuffleCards(dealDeque);

        // set game iterator
        game.setPlayerIterator(players.listIterator(0));

        game.setGameState(IN_PROGRESS);
    }

    private static boolean isActionCard(Card card) {
        return card.value() >= 10;
    }

    public static void playCard(
            Game game,
            Player player,
            @NonNull Card cardPlayed,
            int newColor) throws InvalidPlayException {

        Stack<Card> discardPile = game.getDiscardPile();
        Card topCard = discardPile.peek();

        if (game.getNextDraws() > 0) {
            if (!isDrawCard(cardPlayed)) {
                throw new InvalidPlayException(
                        "You need to draw " + game.getNextDraws() + " cards");
            }
        }


        if (isValidPlay(topCard, cardPlayed)) {
            // remove card from player hand
            player.getHand().remove(cardPlayed);

            if (isActionCard(cardPlayed)) {
                if (isWildCard(cardPlayed)) {
                    // change wild card's color to player's choice of color
                    cardPlayed = new Card(cardPlayed.value(), newColor);
                }
                performAction(game, cardPlayed);
            }

            // add card played to discard pile
            discardPile.push(cardPlayed);

        } else {

            // throw exception if the play is not valid
            throw new InvalidPlayException(
                    "Card: " + cardPlayed + " is not a valid play " +
                            "when top card is: " + topCard);
        }
    }

    private static boolean isDrawCard(Card cardPlayed) {
        return cardPlayed.value() == 12 || cardPlayed.value() == 14;
    }

    private static boolean isWildCard(Card cardPlayed) {
        return cardPlayed.color() == 4;
    }

    private static void performAction(Game game, Card cardPlayed) {
        switch (cardPlayed.value()) {
            case 10 -> game.setSkipNext(true); // skip
            case 11 -> { // reversed
                if (game.getGamePlayDirection().equals(CLOCKWISE)) {
                    game.setGamePlayDirection(COUNTER_CLOCKWISE);
                } else {
                    game.setGamePlayDirection(CLOCKWISE);
                }
            }
            case 12 -> game.setNextDraws(2); // draw two
            case 13 -> {/* wild card already taken care of */}
            case 14 -> game.setNextDraws(4); // draw four
        }
    }

    public static boolean isValidPlay(
            Card topCard,
            Card cardPlayed) {

        // check if is wild card
        if (cardPlayed.color() == 4) return true;

        return topCard.color() == cardPlayed.color() ||
                topCard.value() == cardPlayed.value();
    }
}
