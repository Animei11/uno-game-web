package com.sunkit.unogame.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Message {

    @Builder.Default
    private MessageType messageType = MessageType.MESSAGE;
    private String message;

    public static Message of(String message) {
        return Message.builder()
                .message(message)
                .build();
    }
}
