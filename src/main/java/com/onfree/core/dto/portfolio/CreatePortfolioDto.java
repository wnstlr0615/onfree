package com.onfree.core.dto.portfolio;

import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.portfoliocontent.ImageContent;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.portfoliocontent.TextContent;
import com.onfree.core.entity.portfoliocontent.VideoContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.onfree.core.dto.portfolio.PortfolioContentType.*;

@ApiModel(value = "CreatePortfolioDto")
public class CreatePortfolioDto  {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ApiModel(value = "CreatePortfolioDto_Request")
    public static class Request  {
        @ApiModelProperty(value = "메인이미지 url", notes = "포트폴리오 메인에 등록할 메인이미지", example = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png")
        @NotNull(message = "mainImageUrl은 필 수 입력 항목 입니다.")
        private String mainImageUrl;

        @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목입니다.")
        @NotNull(message = "title은  필수 입력 항목입니다.")
        private String title;

        @ApiModelProperty(value = "포트폴리오 그림 분야", notes = "포트폴리오 그림 분야", dataType = "List")
        @NotNull(message = "그림분야는 입력은 필수 입니다.")
        private List<Long> drawingFieldIds;

        @ApiModelProperty(value = "태그", notes = "검색용 태그 리스트", dataType = "List", example = "캐릭터")
        @NotNull(message = "태그는 입력은 필수 입니다.")
        private List<String> tags;

        @ApiModelProperty(value = "내용", notes = "포트폴리오 내용", dataType = "List")
        @NotEmpty(message = "포트폴리오 내용 입력은 필수 입니다.")
        private List<CreatePortfolioContentDto> contents;

        @ApiModelProperty(value = "임시 저장 유무", notes = "포트폴리오 임시 저장", example = "false")
        @NotNull(message = "temporary 은 필수 입력 항목입니다..")
        private Boolean temporary;
        
        //== 생성 메소드 ==//
        public static CreatePortfolioDto.Request createPortfolioDtoRequest(
                String title, String mainImageUrl, List<String> tags,
                List<CreatePortfolioContentDto> contentDtos, List<Long> drawingFieldIds,  boolean temporary){

            Assert.notNull(tags, "tags must not be null");
            Assert.notNull(contentDtos, "contentDtos must not be null");
            Assert.notEmpty(contentDtos, "contentDtos must not be empty");

            return Request.builder()
                    .title(title)
                    .mainImageUrl(mainImageUrl)
                    .tags(tags)
                    .contents(contentDtos)
                    .drawingFieldIds(drawingFieldIds)
                    .temporary(temporary)
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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @ApiModel(value = "CreatePortfolioDto_Response")
    public static class Response extends RepresentationModel<CreatePortfolioDto.Response>{
        @ApiModelProperty(value = "포트폴리오 PK", notes = "포트폴리오 식별자", example = "1L")
        private Long portfolioId;

        @ApiModelProperty(value = "메인이미지 url", notes = "포트폴리오 메인에 등록할 메인이미지", example = "https://onfree-store.s3.ap-northeast-2.amazonaws.com/portfolio/main-image/test-main-image.png")
        private String mainImageUrl;

        @ApiModelProperty(value = "포트폴리오 제목", notes = "포트폴리오 제목", example = "포트폴리오 제목입니다.")
        @Size(min = 5, message = "포트폴리오 제목은 5자 이상입니다.")
        @NotBlank(message = "포트폴리오 제목은 필 수 입니다.")
        private String title;

        @ApiModelProperty(value = "포트폴리오 그림 분야", notes = "포트폴리오 그림 분야", dataType = "List")
        private List<String> drawingFields;

        @ApiModelProperty(value = "태그", notes = "검색용 태그 리스트", dataType = "List", example = "캐릭터")
        private List<String> tags;

        @ApiModelProperty(value = "내용", notes = "포트폴리오 내용", dataType = "List")
        @NotEmpty(message = "포트폴리오 내용 입력은 필수 입니다.")
        private List<PortfolioContentDetailDto> contents;

        @ApiModelProperty(value = "포트폴리오 상태", notes = "포트폴리오 임시 저장", example = "NORMAL")
        private PortfolioStatus status;

        //== 생성 메소드 ==//
        public static Response fromEntity(Portfolio portfolio) {
            //내용 설정
            List<PortfolioContentDetailDto> contentDetailDtos
                    = portfolio.getPortfolioContents().stream()
                    .map(PortfolioContentDetailDto::fromEntity)
                    .collect(Collectors.toList());

            // 태그 설정
            final List<String> tags
                    = Arrays.stream(portfolio.getTags()
                            .split(","))
                            .filter(StringUtils::hasText)
                            .collect(Collectors.toList());

            // 그림분야 설정
            final List<String> drawingFieldNames = portfolio.getPortfolioDrawingFields().stream()
                    .map(PortfolioDrawingField::getDrawingFieldName)
                    .collect(Collectors.toList());


            return Response.builder()
                    .portfolioId(portfolio.getPortfolioId())
                    .title(portfolio.getTitle())
                    .contents(contentDetailDtos)
                    .mainImageUrl(portfolio.getMainImageUrl())
                    .tags(tags)
                    .drawingFields(drawingFieldNames)
                    .status(portfolio.getStatus())
                    .build();
        }

        public static Response createPortfolioDtoResponse(
                Long portfolioId, String title,  List<PortfolioContentDetailDto> contentDetailDtos,
                String mainImageUrl, List<String> tags, List<String> drawingFieldNames, PortfolioStatus status) {

            return Response.builder()
                    .portfolioId(portfolioId)
                    .title(title)
                    .contents(contentDetailDtos)
                    .mainImageUrl(mainImageUrl)
                    .tags(tags)
                    .drawingFields(drawingFieldNames)
                    .status(status)
                    .build();
        }
    }



}
