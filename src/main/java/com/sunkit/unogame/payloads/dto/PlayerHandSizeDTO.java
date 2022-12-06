package com.sunkit.unogame.payloads.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerHandSizeDTO {
    private String nickName;
    private Integer handSize;
}
