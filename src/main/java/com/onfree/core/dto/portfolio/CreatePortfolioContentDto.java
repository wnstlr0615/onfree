package com.onfree.core.dto.portfolio;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@ApiModel(value = "CreatePortfolioContentDto")
public class CreatePortfolioContentDto {

    @ApiModelProperty(value = "이미지 또는 비디오 타입 url", notes = "이미지 또는 비디오 타입 url", example = "https://www.youtube.com/watch?v=vSY0VEuqeRo&t=146s")
    private final String url;

    @ApiModelProperty(value = "텍스트", notes = "텍스트", example = "텍스트")
    private final String text;

    @ApiModelProperty(value = "블럭 타입", notes = "블럭 타입", example = "TEXT", allowableValues = "TEXT,VIDEO,IMAGE")
    private final String type;

    public static CreatePortfolioContentDto createTextContent(String text){
        return CreatePortfolioContentDto.builder()
                .type(PortfolioContentType.TEXT.name())
                .text(text)
                .build();
    }

    public static CreatePortfolioContentDto createVideoContent(String videoUrl){
        return CreatePortfolioContentDto.builder()
                .type(PortfolioContentType.VIDEO.name())
                .url(videoUrl)
                .build();
    }

    public static CreatePortfolioContentDto createImageContent(String imageUrl){
        return CreatePortfolioContentDto.builder()
                .type(PortfolioContentType.IMAGE.name())
                .url(imageUrl)
                .build();
    }
}
