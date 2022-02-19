package com.onfree.core.dto.user.artist;

import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.BankName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UpdateArtistUserDto {
    @Builder
    @Getter
    @ApiModel(value = "UpdateArtistUser_Request")
    public static class Request{
        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "이름은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        @NotBlank(message = "통신사는 공백일 수 없습니다.")
        private final String newsAgency; //통신사

        @ApiModelProperty(value = "핸드폰번호", example = "010-0000-0000")
        @NotBlank(message = "핸드폰번호는 공백일 수 없습니다.")
        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message="핸드폰번호 패턴이 올바르지 않습니다.")
        private final String phoneNumber;

        @ApiModelProperty(value = "은행명", example = "BUSAN_BANK" ,allowableValues = " ${BankName.joinString()}" )
        @NotNull(message = "은행명은 공백일 수 없습니다.")
        private final BankName bankName;

        @ApiModelProperty(value = "계좌번호", example = "123456-456789-12")
        @NotBlank(message = "계좌번호는 공백일 수 없습니다.")
        private final String accountNumber;

        @ApiModelProperty(value = "성인인증", example = "true")
        @NotNull(message = "성인인증 체크는 필수 입니다.")
        private final Boolean adultCertification;

        @ApiModelProperty(value = "프로필 URL", example = "http://onfree.io/images/546456498")
        @NotBlank(message = "프로필 url은 필수입니다.")
        private final String profileImage;

        @ApiModelProperty(value = "포트폴리오 개인룸 URL", example = "http://onfree.io/portfoliourl/546456498")
        @NotBlank(message = "포트폴리오 개인룸 URL 은 필수입니다.")
        private final String portfolioUrl;

    }
    @Getter
    @Builder
    @ApiModel(value = "UpdateArtistUser_Response")
    public static class Response{
        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "이름은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        @NotBlank(message = "통신사는 공백일 수 없습니다.")
        private final String newsAgency; //통신사

        @ApiModelProperty(value = "핸드폰번호", example = "010-0000-0000")
        @NotBlank(message = "핸드폰번호는 공백일 수 없습니다.")
        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message="핸드폰번호 패턴이 올바르지 않습니다.")
        private final String phoneNumber;

        @ApiModelProperty(value = "은행명", example = "BUSAN_BANK" ,allowableValues = " ${BankName.joinString()}" )
        @NotNull(message = "은행명은 공백일 수 없습니다.")
        private final BankName bankName;

        @ApiModelProperty(value = "계좌번호", example = "123456-456789-12")
        @NotBlank(message = "계좌번호는 공백일 수 없습니다.")
        private final String accountNumber;

        @ApiModelProperty(value = "성인인증", example = "true")
        @NotNull(message = "성인인증 체크는 필수 입니다.")
        private final Boolean adultCertification;

        @ApiModelProperty(value = "프로필 URL", example = "http://onfree.io/images/546456498")
        @NotBlank(message = "프로필 url은 필수입니다.")
        private final String profileImage;

        @ApiModelProperty(value = "포트폴리오 개인룸 URL", example = "http://onfree.io/portfoliourl/546456498")
        @NotBlank(message = "포트폴리오 개인룸 URL 은 필수입니다.")
        private final String portfolioUrl;

        public static UpdateArtistUserDto.Response fromEntity(ArtistUser entity){
            return UpdateArtistUserDto.Response.builder()
                    .nickname(entity.getNickname())
                    .bankName(entity.getBankInfo().getBankName())
                    .accountNumber(entity.getBankInfo().getAccountNumber())
                    .newsAgency(entity.getNewsAgency())
                    .phoneNumber(entity.getPhoneNumber())
                    .profileImage(entity.getProfileImage())
                    .adultCertification(entity.getAdultCertification())
                    .portfolioUrl(entity.getPortfolioRoom().getPortfolioRoomURL())
                    .build();
        }
    }
}
