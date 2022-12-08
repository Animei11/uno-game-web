package com.sunkit.unogame.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkipCurrentPlayerMessage {
    MessageType messageType = MessageType.SKIP_PLAYER;
    String newCurrentPlayerNickname;

    public SkipCurrentPlayerMessage(String newCurrentPlayerNickname) {
        this.newCurrentPlayerNickname = newCurrentPlayerNickname;
    }

    public static SkipCurrentPlayerMessage of(String newCurrentPlayerNickname) {
        return new SkipCurrentPlayerMessage(newCurrentPlayerNickname);
    }
}
