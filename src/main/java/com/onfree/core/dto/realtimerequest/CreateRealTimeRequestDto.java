package com.onfree.core.dto.realtimerequest;

import com.onfree.controller.aws.S3DownLoadController;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.StringUtils;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


public class CreateRealTimeRequestDto extends RepresentationModel<CreateRealTimeRequestDto> {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request{
        @NotBlank(message = "title은 필수 입니다.")
        @Size(min = 5, message = "title 는 5자 이상이여야 합니다.")
        @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
        private String title; // 프로젝트 제목

        @NotBlank(message = "content 는 필수 입니다.")
        @ApiModelProperty(value = "실시간 의뢰 내용", example = "실시간 의뢰 내용")
        private String content; // 프로젝트 내용

        @FutureOrPresent(message = "startDate는 현재보다 과거 일 수 업습니다.")
        @NotNull(message = "startDate 는 필수 입니다.")
        @ApiModelProperty(value = "프로젝트 시작 일", example = "2022-03-07", dataType = "LocalDate")
        private LocalDate startDate; // 시작 일

        @FutureOrPresent(message = "endDate는 현재보다 과거 일 수 업습니다.")
        @NotNull(message = "endDate 는 필수 입니다.")
        @ApiModelProperty(value = "프로젝트 마감 일", example = "2022-03-08", dataType = "LocalDate")
        private LocalDate endDate; // 종료 일

        @NotNull(message = "useType 는 필수 입니다.")
        @ApiModelProperty(value = "의뢰 디자인 용도", example = "COMMERCIAL", allowableValues = "COMMERCIAL,NOT_COMMERCIAL")
        private UseType useType; // 용도

        @ApiModelProperty(value = "참고 링크", example = "http://naver.com")
        private String referenceLink; // 참고 링크

        @NotNull(message = "adult 는 필수 입니다.")
        @ApiModelProperty(value = "성인물 유무", example = "false")
        private Boolean adult; // 성인용 유무

        @AssertTrue(message = "startDate 는 endDate 보다 같거나 작아야 합니다.")
        public boolean validateLocalDateType(){
            return !startDate.isAfter(endDate) && !endDate.isBefore(startDate);
        }

        //== 생성 메서드 ==//
        public static Request createRealTimeRequestDtoRequest(
                String title, String content,  LocalDate startDate, LocalDate endDate,
                UseType useType, String referenceLink, Boolean adult) {
            return Request.builder()
                    .title(title)
                    .content(content)
                    .startDate(startDate)
                    .endDate(endDate)
                    .useType(useType)
                    .referenceLink(referenceLink)
                    .adult(adult)
                    .build();
        }

        public RealTimeRequest toEntity() {
            return RealTimeRequest.builder()
                    .title(title)
                    .content(content)
                    .startDate(startDate)
                    .endDate(endDate)
                    .useType(useType)
                    .referenceLink(referenceLink)
                    .adult(adult)
                    .status(RequestStatus.REQUEST_RECRUITING)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response extends RepresentationModel<Response>{
        @ApiModelProperty(value = "실시간 의뢰 Pk", example = "1L")
        private Long realTimeRequestId;

        @ApiModelProperty(value = "실시간 의뢰 제목", example = " 실시간 의뢰 제목")
        private String title; // 프로젝트 제목

        @ApiModelProperty(value = "실시간 의뢰 내용", example = "실시간 의뢰 내용")
        private String content; // 프로젝트 내용

        @ApiModelProperty(value = "프로젝트 시작 일", example = "2022-03-07", dataType = "LocalDate")
        private LocalDate startDate; // 시작 일

        @ApiModelProperty(value = "프로젝트 마감 일", example = "2022-03-08", dataType = "LocalDate")
        private LocalDate endDate; // 종료 일

        @ApiModelProperty(value = "의뢰 디자인 용도", example = "COMMERCIAL", allowableValues = "COMMERCIAL,NOT_COMMERCIAL")
        private UseType useType; // 용도

        @ApiModelProperty(value = "참고 링크", example = "http://naver.com")
        private String referenceLink; // 참고 링크

        @ApiModelProperty(value = "참고파일 링크", example = "http://localhost:8080/reference-files/a8b78a16-083f-4ba7-b204-563b3ede54ac.csv")
        private List<String> referenceFileUrlList;

        @ApiModelProperty(value = "성인물 유무", example = "false")
        private Boolean adult; // 성인용 유무

        public static Response fromEntity(RealTimeRequest realTimeRequest) {
            String referenceFiles = realTimeRequest.getReferenceFiles();
            List<String> referenceFileUrlList = new ArrayList<>();

            if(StringUtils.hasText(referenceFiles)){
                referenceFileUrlList = Arrays.stream(referenceFiles.split(","))
                        .map(filename -> linkTo(methodOn(S3DownLoadController.class).getReferenceFile(filename)).toString())
                        .collect(Collectors.toList());
            }

            return Response.builder()
                    .realTimeRequestId(realTimeRequest.getRealTimeRequestId())
                    .title(realTimeRequest.getTitle())
                    .content(realTimeRequest.getContent())
                    .startDate(realTimeRequest.getStartDate())
                    .endDate(realTimeRequest.getEndDate())
                    .useType(realTimeRequest.getUseType())
                    .referenceLink(realTimeRequest.getReferenceLink())
                    .referenceFileUrlList(referenceFileUrlList)
                    .adult(realTimeRequest.getAdult())
                    .build();
        }

        public static Response createRealTimeRequestDtoResponse(
                Long realTimeRequestId, String title, String content,  LocalDate startDate,
                LocalDate endDate, UseType useType, String referenceLink, Boolean adult
        ) {
            return Response.builder()
                    .realTimeRequestId(realTimeRequestId)
                    .title(title)
                    .content(content)
                    .startDate(startDate)
                    .endDate(endDate)
                    .useType(useType)
                    .referenceLink(referenceLink)
                    .adult(adult)
                    .build();
        }
    }

}
