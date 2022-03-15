package com.onfree.core.entity.portfoliocontent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@DiscriminatorValue(value = "I")
@AllArgsConstructor
public class ImageContent extends PortfolioContent {
    String imageUrl;

    public static ImageContent createImageContent(String imageUrl){
        return ImageContent.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
