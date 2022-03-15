package com.onfree.core.entity.realtimerequset;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UseType {
    COMMERCIAL("상업용"),
    NOT_COMMERCIAL("비 상업용")
    ;

    private final String description;
}
