package com.onfree.core.dto.user.normal;

import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.user.BankName;
import com.onfree.core.entity.user.NormalUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;

public class UpdateNormalUserDto {
    @ApiModel(value = "UpdaetNormalUser_Request")
    @Builder
    @Getter
    public static class Request{
        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "이름은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        @NotNull(message = "통신사는 공백일 수 없습니다.")
        private final MobileCarrier mobileCarrier; //통신사

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

    }
    @Getter
    @Builder
    @ApiModel(value = "UpdateNormalUser_Response")
    public static class Response{
        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "이름은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        @NotBlank(message = "통신사는 공백일 수 없습니다.")
        private final MobileCarrier mobileCarrier; //통신사

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

        public static UpdateNormalUserDto.Response fromEntity(NormalUser entity){
            return Response.builder()
                    .nickname(entity.getNickname())
                    .bankName(entity.getBankInfo().getBankName())
                    .accountNumber(entity.getBankInfo().getAccountNumber())
                    .mobileCarrier(entity.getMobileCarrier())
                    .phoneNumber(entity.getPhoneNumber())
                    .profileImage(entity.getProfileImage())
                    .adultCertification(entity.getAdultCertification())
                    .build();
        }
    }
}
