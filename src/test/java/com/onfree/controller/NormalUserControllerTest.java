package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.config.webmvc.resolver.CurrentNormalUserArgumentResolver;
import com.onfree.controller.user.NormalUserController;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.user.NormalUserService;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NormalUserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class NormalUserControllerTest extends ControllerBaseTest {
    @MockBean
    NormalUserService normalUserService;
    @MockBean
    CurrentNormalUserArgumentResolver currentNormalUserArgumentResolver;

    @Test
    @WithAnonymousUser
    @DisplayName("[??????][POST] ???????????? ??????")
    public void givenCreateUserReq_whenCreateNormalUser_thenCreateUserRes() throws Exception{

        //given
        CreateNormalUserDto.Request request = givenCreateNormalUserReq();
        CreateNormalUserDto.Response response = givenCreateNormalUserRes(request);
        when(normalUserService.addNormalUser(any()))
                .thenReturn(response);

        //when //then
        mvc.perform(post("/api/v1/users/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("??????"))
            .andExpect(jsonPath("$.nickname").value("???????????????"))
            .andExpect(jsonPath("$.email").value("jun@naver.com"))
            .andExpect(jsonPath("$.mobileCarrier").value(MobileCarrier.SKT.toString()))
            .andExpect(jsonPath("$.phoneNumber").value("010-8888-9999"))
            .andExpect(jsonPath("$.bankName").value(BankName.IBK.name()))
            .andExpect(jsonPath("$.accountNumber").value("010-8888-9999"))
            .andExpect(jsonPath("$.serviceAgree").value(true))
            .andExpect(jsonPath("$.policyAgree").value(true))
            .andExpect(jsonPath("$.personalInfoAgree").value(true))
            .andExpect(jsonPath("$.advertisementAgree").value(true))
            .andExpect(jsonPath("$.adultCertification").value(true))
            .andExpect(jsonPath("$.gender").value(Gender.MAN.name()))
            .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/123456789"))
        ;
        verify(normalUserService, times(1)).addNormalUser(any());
    }


    private CreateNormalUserDto.Request givenCreateNormalUserReq() {
        return CreateNormalUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("???????????????")
                .name("??????")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankName(BankName.IBK)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(true)
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }
    private CreateNormalUserDto.Response givenCreateNormalUserRes(CreateNormalUserDto.Request request){
        return CreateNormalUserDto.Response
                .builder()
                .adultCertification(request.getAdultCertification())
                .email(request.getEmail())
                .gender(request.getGender())
                .name(request.getName())
                .nickname(request.getNickname())
                .mobileCarrier(request.getMobileCarrier())
                .phoneNumber(request.getPhoneNumber())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .advertisementAgree(request.getAdvertisementAgree())
                .personalInfoAgree(request.getPersonalInfoAgree())
                .policyAgree(request.getPolicyAgree())
                .serviceAgree(request.getServiceAgree())
                .profileImage(request.getProfileImage())
                .build();
    }


    private CreateNormalUserDto.Request givenWrongCreateNormalUserReq() {
        return CreateNormalUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankName(null)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(false)
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[??????][POST] ???????????? ?????? - ????????? ???????????? ?????? ???????????? ??????")
    public void givenDuplicatedEmail_whenCreateNormalUser_thenDuplicatedEmailError() throws Exception{
        //given
        CreateNormalUserDto.Request request = givenCreateNormalUserReq();
        UserErrorCode errorCode = UserErrorCode.USER_EMAIL_DUPLICATED;
        when(normalUserService.addNormalUser(any()))
                .thenThrow( new UserException(errorCode));
        //when //then
        mvc.perform(post("/api/v1/users/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).addNormalUser(any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[??????][GET] ????????? ?????? ?????? ")
    public void givenUserId_whenGetUserInfo_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        when(normalUserService.getUserDetail(anyLong()))
                .thenReturn(
                        getNormalUserInfo()
                );
        when(currentNormalUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentNormalUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getNormalUser(1L));
        //when//then
        mvc.perform(get("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("??????"))
                .andExpect(jsonPath("$.email").value("jun@naver.com"))
                .andExpect(jsonPath("$.mobileCarrier").value(MobileCarrier.SKT.toString()))
                .andExpect(jsonPath("$.phoneNumber").value("010-8888-9999"))
                .andExpect(jsonPath("$.bankName").value(BankName.IBK.name()))
                .andExpect(jsonPath("$.accountNumber").value("010-8888-9999"))
                .andExpect(jsonPath("$.serviceAgree").value(true))
                .andExpect(jsonPath("$.policyAgree").value(true))
                .andExpect(jsonPath("$.personalInfoAgree").value(true))
                .andExpect(jsonPath("$.advertisementAgree").value(true))
                .andExpect(jsonPath("$.adultCertification").value(true))
                .andExpect(jsonPath("$.gender").value(Gender.MAN.name()))
                .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/123456789"))
                ;
        verify(normalUserService, times(1)).getUserDetail(any());
    }
    public NormalUser getNormalUser(long userId){
        return NormalUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("??????")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankInfo(
                        BankInfo.createBankInfo(BankName.IBK, "010-8888-9999")
                )
                .userAgree(
                        UserAgree.createUserAgree(true,true,true,true)
                )
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }

    public NormalUserDetailDto getNormalUserInfo(){
            return NormalUserDetailDto
                    .fromEntity(
                            getNormalUserEntityFromCreateNormalUserRequest()
                    );
    }

    public NormalUser getNormalUserEntityFromCreateNormalUserRequest(){
            return givenCreateNormalUserReq().toEntity();
    }

    @Test
    @WithArtistUser
    @DisplayName("[??????][GET] ????????? ?????? ?????? - ??????????????? ????????? ??????")
    public void givenUserId_whenGetUserInfoWithArtistUser_thenReturnUserInfo() throws Exception {
        //given
        final Long userId = 1L;
        when(normalUserService.getUserDetail(anyLong()))
                .thenReturn(
                        getNormalUserInfo()
                );
        //when

        //then
        mvc.perform(get("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).getUserDetail(any());
    }


    @Test
    @WithNormalUser
    @DisplayName("[??????][DELETE] ????????? ?????? Flag ??????")
    public void givenDeleteUserId_whenDeleteNormalUser_thenReturnDeletedUserResponse() throws Exception{

        //given
        final long deletedUserId = 1L;
        doNothing().when(normalUserService)
                .removeNormalUser(deletedUserId);
        when(currentNormalUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentNormalUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getNormalUser(1L));

        //when && then
        mvc.perform(delete("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("???????????? ??????????????? ?????????????????????."))
        ;
        verify(normalUserService, times(1)).removeNormalUser(any());
    }

    private DeletedUserResponse getDeletedUserResponse(long userId) {
        return DeletedUserResponse.builder()
                .userId(userId)
                .deleted(true)
                .build();
    }
    
    @Test
    @WithArtistUser
    @DisplayName("[??????][DELETE] ????????? ?????? Flag ?????? - ??????????????? ????????? ??????")
    public void givenDeleteUserId_whenDeleteNormalUserWithArtistUser_thenReturnDeletedUserResponse() throws Exception{
        //given
        final long deletedUserId = 1L;
        //when && then
        mvc.perform(delete("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).removeNormalUser(any());
    }

    @Test
    @WithNormalUser
    @DisplayName("[??????][DELETE] ????????? ?????? Flag ?????? - ?????? ???????????? ????????? ??????")
    public void givenAlreadyDeleteUserId_whenDeleteNormalUser_thenAlreadyUserDeleted() throws Exception{
        //given
        final long deletedUserId = 1L;
        final UserErrorCode errorCode = UserErrorCode.ALREADY_USER_DELETED;
        doThrow(new UserException(errorCode))
                .when(normalUserService).removeNormalUser(deletedUserId);
        when(currentNormalUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentNormalUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getNormalUser(1L));
        //when && then
        mvc.perform(delete("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(normalUserService, times(1)).removeNormalUser(any());
    }
    @Test
    @WithNormalUser
    @DisplayName("[??????][PUT] ????????? ?????? ??????")
    public void givenUpdateUserInfo_whenModifiedUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        doNothing().when(normalUserService).modifyNormalUser(anyLong(), any());

        when(currentNormalUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentNormalUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getNormalUser(1L));
        //when then
        mvc.perform(put("/api/v1/users/normal/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateNormalUserReq()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("????????? ????????? ??????????????? ?????? ???????????????."))
        ;
        verify(normalUserService, times(1)).modifyNormalUser(eq(userId), any());
    }

    private UpdateNormalUserDto.Response getUpdateNormalUserRes() {
        return UpdateNormalUserDto.Response.builder()
                .nickname("???????????????")
                .bankName(BankName.IBK)
                .accountNumber("010-0000-0000")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }

    private UpdateNormalUserDto.Request givenUpdateNormalUserReq() {
        return UpdateNormalUserDto.Request.builder()
                .nickname("???????????????")
                .bankName(BankName.IBK)
                .accountNumber("010-0000-0000")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }
    @Test
    @WithArtistUser
    @DisplayName("[??????][PUT] ????????? ?????? ?????? - ??????????????? ????????? ??????")
    public void givenUpdateUserInfo_whenModifiedUserWithArtistUser_thenReturnUpdateInfo() throws Exception{
        //given
        final long userId = 1L;
        //when then
        mvc.perform(put("/api/v1/users/normal/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateNormalUserReq()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
        verify(normalUserService, never()).modifyNormalUser(eq(userId), any());
    }



    private UpdateNormalUserDto.Request givenWrongUpdateNormalUserReq() {
        return UpdateNormalUserDto.Request.builder()
                .nickname("???????????????")
                .accountNumber("010-0000-0000")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }


}
