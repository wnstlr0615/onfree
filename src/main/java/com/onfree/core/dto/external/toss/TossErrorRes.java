package com.onfree.core.dto.external.toss;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TossErrorRes {
    private final String code;
    private final String message;
}
