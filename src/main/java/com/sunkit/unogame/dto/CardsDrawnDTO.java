package com.sunkit.unogame.dto;

import com.sunkit.unogame.model.Card;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CardsDrawnDTO {
    private List<Card> cardsDrawn;

    public static CardsDrawnDTO of(List<Card> cardsDrawn) {
        return CardsDrawnDTO.builder()
                .cardsDrawn(cardsDrawn)
                .build();
    }
}
