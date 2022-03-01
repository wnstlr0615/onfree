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
@AllArgsConstructor
@DiscriminatorValue(value = "V")
public class VideoContent extends PortfolioContent {
    String videoUrl;

    public static VideoContent createVideoContent(String videoUrl){
        return VideoContent.builder()
                .videoUrl(videoUrl)
                .build();
    }
 }
