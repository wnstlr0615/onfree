package com.onfree.core.dto.portfolio;

import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Getter
@Builder
@ApiModel(description = "포트폴리오 상세 조회 DTO")
public class PortfolioDetailDto extends RepresentationModel<PortfolioDetailDto> {
    @ApiModelProperty(value = "포트폴리오 PK", notes = "포트폴리오 PK ", example = "1")
    private final Long portfolioId;

    @ApiModelProperty(value = "작가유저 이메일", notes = "작가유저 이메일(닉네임으로 대체 가능) ", example = "joon@naver.com")
    private final String artistUser;

    @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목입니다.")
    private final String title;

    @Setter
    @ApiModelProperty(value = "포트폴리오 그림 분야", notes = "포트폴리오 그림 분야", dataType = "List")
    private List<UsedDrawingFieldDto> drawingFields;


    @ApiModelProperty(value = "포트폴리오 내용", notes = "포트폴리오 내용", dataType = "List")
    private List<PortfolioContentDetailDto> contents;

    @ApiModelProperty(value = "검색용 태그", notes = "포트폴리오 검색 태그", dataType = "List")
    private List<String> tags;

    @ApiModelProperty(value = "포트폴리오 상태", notes = "포트폴리오 상태", example = "NORMAL", allowableValues = "NORMAL,REPRESENTATION,TEMPORARY,DELETED,HIDDEN")
    private PortfolioStatus status;

    //== 생성 메소드 ==//
    public static PortfolioDetailDto createPortfolioDetailDto(
            Long portfolioId, String artistUser, String title, PortfolioStatus status,
            List<String> tags, List<PortfolioContentDetailDto> contents, List<UsedDrawingFieldDto> usedDrawingFieldDtos){
        Assert.notNull(tags, "tags must not be null");
        Assert.notNull(contents, "contents must not be null");
        Assert.notNull(status, "portfolio status must not be null");
        return PortfolioDetailDto.builder()
                .portfolioId(portfolioId)
                .artistUser(artistUser)
                .title(title)
                .status(status)
                .tags(tags)
                .contents(contents)
                .drawingFields(usedDrawingFieldDtos)
                .build();
    }

    public static PortfolioDetailDto fromEntity(Portfolio portfolio) {
        final List<PortfolioContentDetailDto> contentDetailDtos
                = portfolio.getPortfolioContents().stream()
                .map(PortfolioContentDetailDto::fromEntity)
                .collect(Collectors.toList());

        List<String> tags = new ArrayList<>();
        if(StringUtils.hasText(portfolio.getTags())){
            tags = Arrays.stream(
                    portfolio.getTags().split(",")
            ).collect(Collectors.toList());
        }



        return PortfolioDetailDto.builder()
                .portfolioId(portfolio.getPortfolioId())
                .artistUser(portfolio.getArtistUser().getEmail())
                .title(portfolio.getTitle())
                .tags(tags)
                .contents(contentDetailDtos)
                .status(portfolio.getStatus())
                .build();
    }
}
