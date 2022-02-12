package com.onfree.core.dto.portfolio;

import com.onfree.core.entity.Portfolio;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortfolioSimpleDto {
    @ApiModelProperty(value = "포트폴리오 PK", notes = "포트폴리오 PK", example = "1")
    private final Long portfolioId;

    @ApiModelProperty(value = "포트폴리오 메인 이미지", notes = "포트폴리오 메인 이미지", example = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png")
    private final String mainImageUrl;

    @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목")
    private final String title;

    @ApiModelProperty(value = "조회수", notes = "조회수", example = "999")
    private final Long view;

    @ApiModelProperty(value = "대표 포트폴리오 설정 유무", notes = "대표 포트폴리오 설정 유무", example = "false")
    private final boolean representative;

    public static PortfolioSimpleDto createPortfolioSimpleDto(long portfolioId, String title, String mainImageUrl, boolean representative){
        return PortfolioSimpleDto.builder()
                .portfolioId(portfolioId)
                .title(title)
                .mainImageUrl(mainImageUrl)
                .view(0L)
                .representative(representative)
                .build();
    }

    public static PortfolioSimpleDto fromEntity(Portfolio portfolio) {
        return PortfolioSimpleDto.builder()
                .portfolioId(portfolio.getPortfolioId())
                .title(portfolio.getTitle())
                .mainImageUrl(portfolio.getMainImageUrl())
                .view(portfolio.getView())
                .representative(portfolio.isRepresentative())
                .build();
    }
}
