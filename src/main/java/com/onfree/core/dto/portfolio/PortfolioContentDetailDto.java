package com.onfree.core.dto.portfolio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onfree.core.entity.portfoliocontent.ImageContent;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.portfoliocontent.TextContent;
import com.onfree.core.entity.portfoliocontent.VideoContent;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioContentDetailDto {
    private final String url;
    private final String text;
    private final String type;

    public static PortfolioContentDetailDto fromEntity(PortfolioContent portfolioContent) {
        if(portfolioContent instanceof ImageContent){
            return createImagePortfolioContentDetailDto(((ImageContent) portfolioContent).getImageUrl());
        }else if(portfolioContent instanceof VideoContent){
            return createVideoPortfolioContentDetailDto(((VideoContent) portfolioContent).getVideoUrl());
        }else {
            return createTextPortfolioContentDetailDto(((TextContent) portfolioContent).getText());
        }
    }

    public static PortfolioContentDetailDto createImagePortfolioContentDetailDto(String imageUrl){
        return PortfolioContentDetailDto.builder()
                .type(PortfolioContentType.IMAGE.name())
                .url(imageUrl)
                .build();
    }
    public static PortfolioContentDetailDto createVideoPortfolioContentDetailDto(String videoUrl){
        return PortfolioContentDetailDto.builder()
                .type(PortfolioContentType.VIDEO.name())
                .url(videoUrl)
                .build();
    }
    public static PortfolioContentDetailDto createTextPortfolioContentDetailDto(String text){
        return PortfolioContentDetailDto.builder()
                .type(PortfolioContentType.TEXT.name())
                .text(text)
                .build();
    }
}
