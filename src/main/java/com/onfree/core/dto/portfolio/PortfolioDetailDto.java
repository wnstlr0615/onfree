package com.onfree.core.dto.portfolio;

import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.Portfolio;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Getter
@Builder
@ApiModel(description = "포트폴리오 상세 조회 DTO")
public class PortfolioDetailDto {
    @ApiModelProperty(value = "포트폴리오 PK", notes = "포트폴리오 PK ", example = "1")
    private final Long portfolioId;

    @ApiModelProperty(value = "작가유저 이메일", notes = "작가유저 이메일(닉네임으로 대체 가능) ", example = "joon@naver.com")
    private final String artistUser;

    @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목입니다.")
    private final String title;

    @Setter
    @ApiModelProperty(value = "포트폴리오 그림 분야", notes = "포트폴리오 그림 분야", dataType = "List")
    private List<UsedDrawingFieldDto> drawingFields = new ArrayList<>();


    @ApiModelProperty(value = "포트폴리오 내용", notes = "포트폴리오 내용", dataType = "List")
    private List<PortfolioContentDetailDto> contents = new ArrayList<>();

    @ApiModelProperty(value = "검색용 태그", notes = "포트폴리오 검색 태그", dataType = "List")
    private List<String> tags = new ArrayList<>();

    @ApiModelProperty(value = "대표 포트폴리오 설정", notes = "대표 포트폴리오 설정", example = "false")
    private final boolean representative;

    @ApiModelProperty(value = "임시 저장", notes = "임시 저장", example = "false")
    private final boolean temporary;

    //== 생성 메소드 ==//
    public static PortfolioDetailDto createPortfolioDetailDto(
            Long portfolioId, String artistUser, String title, boolean representative, boolean temporary,
            List<String> tags, List<PortfolioContentDetailDto> contents, List<UsedDrawingFieldDto> usedDrawingFieldDtos){
        Assert.notNull(tags, "tags must not be null");
        Assert.notNull(contents, "contents must not be null");

        return PortfolioDetailDto.builder()
                .portfolioId(portfolioId)
                .artistUser(artistUser)
                .title(title)
                .representative(representative)
                .tags(tags)
                .contents(contents)
                .drawingFields(usedDrawingFieldDtos)
                .temporary(temporary)
                .build();
    }

    public static PortfolioDetailDto fromEntity(Portfolio portfolio) {
        final List<PortfolioContentDetailDto> contentDetailDtos = portfolio.getPortfolioContents().stream()
                .map(PortfolioContentDetailDto::fromEntity)
                .collect(Collectors.toList());

        final List<String> tags = Arrays.stream(portfolio.getTags().split(",")).collect(Collectors.toList());
        return PortfolioDetailDto.builder()
                .portfolioId(portfolio.getPortfolioId())
                .artistUser(portfolio.getArtistUser().getEmail())
                .title(portfolio.getTitle())
                .tags(tags)
                .representative(portfolio.isRepresentative())
                .temporary(portfolio.isTemporary())
                .contents(contentDetailDtos)
                .build();
    }
}
