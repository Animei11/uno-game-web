package com.sunkit.unogame.payloads.responses;

import com.sunkit.unogame.payloads.dto.GameUpdateDTO;
import lombok.Builder;
import lombok.Data;

@Data
public class StartGameMessage {
    @Builder.Default
    MessageType messageType = MessageType.START;

    GameUpdateDTO gameUpdate;
    public StartGameMessage(GameUpdateDTO gameUpdateDTO) {
        this.gameUpdate = gameUpdateDTO;
    }
}
