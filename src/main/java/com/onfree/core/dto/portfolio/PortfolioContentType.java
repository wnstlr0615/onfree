package com.onfree.core.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
@AllArgsConstructor
public enum PortfolioContentType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO")
    ;
    private final String description;
    public  boolean equalContentType(@NonNull String contentType){
        return this.name().equals(contentType);
    }
}
