package com.sunkit.unogame.payloads.dto;

import com.sunkit.unogame.model.Card;
import com.sunkit.unogame.payloads.responses.MessageType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CardsDrawnDTO {

    @Builder.Default
    private MessageType messageType = MessageType.CARDS_DRAWN;
    private List<Card> cardsDrawn;

    public static CardsDrawnDTO of(List<Card> cardsDrawn) {
        return CardsDrawnDTO.builder()
                .cardsDrawn(cardsDrawn)
                .build();
    }
}
