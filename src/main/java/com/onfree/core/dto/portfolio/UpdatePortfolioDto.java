package com.onfree.core.dto.portfolio;

import com.onfree.core.entity.portfoliocontent.ImageContent;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.portfoliocontent.TextContent;
import com.onfree.core.entity.portfoliocontent.VideoContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static com.onfree.core.dto.portfolio.PortfolioContentType.*;


@Builder
@Getter
public class UpdatePortfolioDto {

    @ApiModelProperty(value = "메인이미지 url", notes = "포트폴리오 메인에 등록할 메인이미지", example = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png")
    private final String mainImageUrl;

    @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목입니다.")
    @Size(min = 5, message = "포트폴리오 제목은 5자 이상입니다.")
    @NotBlank(message = "포트폴리오 제목은 필 수 입니다.")
    private final String title;

    @ApiModelProperty(value = "태그", notes = "검색용 태그 리스트", dataType = "List", example = "캐릭터")
    @NotNull(message = "포트폴리오 태그가 null 입니다.")
    private List<String> tags = new ArrayList<>();

    @ApiModelProperty(value = "내용", notes = "포트폴리오 내용", dataType = "List")
    @NotEmpty(message = "포트폴리오 내용 입력은 필수 입니다.")
    private List<CreatePortfolioContentDto> contents = new ArrayList<>();

    @ApiModelProperty(value = "포트폴리오 그림 분야", notes = "포트폴리오 그림 분야", dataType = "List")
    private List<Long> drawingFieldIds = new ArrayList<>();

    @ApiModelProperty(value = "임시 저장 유무", notes = "포트폴리오 임시 저장", example = "false")
    @NotNull(message = "임시저장 유무는 필수 입니다.")
    private final Boolean temporary;

    public static UpdatePortfolioDto createUpdatePortfolioDto(String mainImageUrl, String title, List<String> tags, List<Long> drawingFieldIds, boolean temporary, List<CreatePortfolioContentDto> contents) {
        Assert.notNull(tags, "tags must not be null");
        Assert.notNull(drawingFieldIds, "drawingFieldIds must not be null");
        Assert.notNull(contents, "contents must not be null");

        return UpdatePortfolioDto.builder()
                .mainImageUrl(mainImageUrl)
                .title(title)
                .tags(tags)
                .drawingFieldIds(drawingFieldIds)
                .temporary(temporary)
                .contents(contents)
                .build();
    }

    public List<PortfolioContent> toPortfolioContentList(){
        List<PortfolioContent> portfolioContentList = new ArrayList<>();
        for (CreatePortfolioContentDto content : contents) {
            final String contentType = content.getType();

            if (TEXT.equalContentType(contentType)) {
                portfolioContentList.add(new TextContent(content.getText()));
            }else if(IMAGE.equalContentType(contentType)){
                portfolioContentList.add(new ImageContent(content.getUrl()));
            }else if(VIDEO.equalContentType(contentType)){
                portfolioContentList.add(new VideoContent(content.getUrl()));
            }else{
                break;
            }
        }
        return portfolioContentList;
    }
}
