package com.onfree.core.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerifyResult {
    private final boolean result;
    private final String username;
}
