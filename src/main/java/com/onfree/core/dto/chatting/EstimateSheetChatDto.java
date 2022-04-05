package com.onfree.core.dto.chatting;

import com.onfree.core.entity.chatting.EstimateSheetChat;
import com.onfree.core.entity.realtimerequset.UseType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


public class EstimateSheetChatDto {
   @AllArgsConstructor
   @NoArgsConstructor(access = AccessLevel.PROTECTED)
   @Builder
   @Getter
   public static class Request{

      @ApiModelProperty(value = "프로젝트 제목")
      @NotBlank(message = "title은 공백일 수 없습니다.")
      private String title; // 프로젝트 제목

      @ApiModelProperty(value = "프로젝트 내용")
      @NotBlank(message = "content는 공백일 수 없습니다.")
      private String content; // 작업 내용

      @ApiModelProperty(value = "프로젝트 시작일")
      @NotNull(message = "startDate는 null일 수 없습니다.")
      private LocalDate startDate; // 시작 일

      @ApiModelProperty(value = "프로젝트 종료 일")
      @NotNull(message = "endDate는 null일 수 없습니다.")
      private LocalDate endDate; // 종료 일

      @ApiModelProperty(value = "견적 금액")
      @NotNull(message = "amount는 null일 수 없습니다.")
      private Long estimatedAmount; // 견적 금액

      @ApiModelProperty(value = "수정 범위 및 횟수(수정 조건) ")
      @NotBlank(message = "conditionNote는 공백일 수 없습니다.")
      private String conditionNote;

      @ApiModelProperty(value = "제공하는 결과물 ")
      @NotBlank(message = "offerResult 는 공백일 수 없습니다.")
      private String offerResult;

   }

   @AllArgsConstructor
   @NoArgsConstructor(access = AccessLevel.PROTECTED)
   @Builder
   @Getter
   public static class Response extends RepresentationModel<Response> {
      @ApiModelProperty(value = "프로젝트 제목")
      private String title; // 프로젝트 제목

      @ApiModelProperty(value = "프로젝트 내용")
      private String content; // 작업 내용

      @ApiModelProperty(value = "프로젝트 시작일")
      private LocalDate startDate; // 시작 일

      @ApiModelProperty(value = "프로젝트 종료일")
      private LocalDate endDate; // 종료 일

      @ApiModelProperty(value = "견적 금액")
      private Long estimatedAmount; // 견적 금액

      @ApiModelProperty(value = "결제 금액")
      private Long paymentAmount; // 결제 금액

      @ApiModelProperty(value = "수정 범위 및 횟수(수정 조건) ")
      private String conditionNote;

      @ApiModelProperty(value = "제공하는 결과물 ")
      private String offerResult;

      private String sender; // 보내는 사람

      private String receiver; // 받는 사람

      private String type; // 타입

      private String orderId;

      public static Response fromEntity(EstimateSheetChat entity) {
         return Response.builder()
                 .title(entity.getTitle())
                 .content(entity.getContent())
                 .startDate(entity.getStartDate())
                 .endDate(entity.getEndDate())
                 .estimatedAmount(entity.getEstimatedAmount())
                 .paymentAmount(entity.getPaymentAmount())
                 .conditionNote(entity.getConditionNote())
                 .offerResult(entity.getOfferResult())
                 .sender(entity.getSender().getNickname())
                 .receiver(entity.getReceiver().getNickname())
                 .type(ChatType.ESTIMATE_SHEET.name())
                 .orderId(entity.getOrderId())
                 .build();
      }
   }
}
