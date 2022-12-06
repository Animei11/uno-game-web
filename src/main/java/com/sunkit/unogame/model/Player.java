package com.sunkit.unogame.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Player {
    String nickname;
    List<Card> hand;
}
