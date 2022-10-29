package com.sunkit.unogame.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String message;

    public static Message of(String message) {
        return new Message(message);
    }
}
