package com.onfree.core.dto.user.artist;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MobileCarrier {
    KT("KT"),
    LG("LG"),
    SKT("SKT")
    ;
    private final String description;
}
