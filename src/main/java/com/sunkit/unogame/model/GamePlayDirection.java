package com.sunkit.unogame.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GamePlayDirection {
    CLOCKWISE(true),
    COUNTER_CLOCKWISE(false);

    public final boolean isClockwise;
}
