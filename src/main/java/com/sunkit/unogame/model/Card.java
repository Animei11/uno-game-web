package com.sunkit.unogame.model;

import lombok.SneakyThrows;

import java.util.Comparator;

public record Card(int value, int color) implements Comparable<Card> {

    @SneakyThrows
    public Card {
        if (color < 0 || color > 4) {
            throw new Exception("Invalid color value: " + color);
        }
    }

    @Override
    public String toString() {
        String displayColor = "";
        switch (color) {
            case 0 -> displayColor = "Red";
            case 1 -> displayColor = "Yellow";
            case 2 -> displayColor = "Blue";
            case 3 -> displayColor = "Green";
            case 4 -> displayColor = "Wild Card";
        }
        String displayValue = "";
        if (value < 10) displayValue = String.valueOf(value);
        displayValue = switch (value) {
            case 10 -> "skip";
            case 11 -> "reverse";
            case 12 -> "draw two";
            case 13 -> "wild";
            case 14 -> "draw four";
            default -> displayValue;
        };

        return String.format("[%s,%s]", displayValue, displayColor);
    }

    @Override
    public int compareTo(Card other) {
        return Card.getComparator().compare(this, other);
    }

    public static Comparator<Card> getComparator() {
        return (card1, card2) -> {
            int result = Integer.compare(card1.color, card2.color);
            if (result != 0)
                return result;
            else
                return Integer.compare(card1.value, card2.value);
        };
    }
}
