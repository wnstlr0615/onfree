package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.ArtistUserDrawingFieldService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserDrawingFieldController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtistUserDrawingFieldControllerTest extends ControllerBaseTest {
    @MockBean
    ArtistUserDrawingFieldService artistUserDrawingFieldService;

    @MockBean
    CurrentArtistUserArgumentResolver currentArtistUserArgumentResolver;

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 그림분야 변경")
    public void givenUpdateDrawingFieldsDto_whenUpdateDrawingFields_then_SimpleSuccess() throws Exception{
        //given

        doNothing().when(artistUserDrawingFieldService)
                .updateDrawingFields(anyLong(), any(UpdateDrawingFieldsDto.class));
        when(currentArtistUserArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        //when
        //then
        mvc.perform(put("/api/v1/users/artist/me/drawing-fields")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            givenUpdateDrawingFieldsDto()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("그림분야가 성공적으로 등록되었습니다."))
        ;
        verify(artistUserDrawingFieldService).updateDrawingFields(anyLong(), any(UpdateDrawingFieldsDto.class));
    }

    private UpdateDrawingFieldsDto givenUpdateDrawingFieldsDto() {

        return UpdateDrawingFieldsDto.builder()
                .drawingFields(
                        List.of(1L, 2L, 3L)
                ).build();
    }
    private ArtistUser getArtistUser() {
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK)
                .build();
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();
        return ArtistUser.builder()
                .userId(1L)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-0000-0000")
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(true)
                .profileImage("http://www.onfree.co.kr/images/dasdasfasd")
                .deleted(false)
                .role(Role.ARTIST)
                .portfolioUrl("http://www.onfree.co.kr/folioUrl/dasdasfasd")
                .build();
    }

    @Test
    @DisplayName("[성공][GET] 작가유저 그림 분야 목록 조회")
    public void givenUserId_whenGetAllArtistUserDrawingFields_thenSuccessArtistUserDrawingFieldDtoList() throws Exception{
        //given
        final long givenUserId = 1L;
        when(artistUserDrawingFieldService.getAllArtistUserUsedDrawingFields(anyLong()))
                .thenReturn(getUsedDrawingFieldDtoList());
        //when
        //then
        mvc.perform(get("/api/v1/users/artist/{userId}/drawing-fields", givenUserId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].drawingFieldId").value(1))
            .andExpect(jsonPath("$.[0].drawingFieldName").value("캐릭터 디자인"))
            .andExpect(jsonPath("$.[0].used").value(true))
        ;
        verify(artistUserDrawingFieldService).getAllArtistUserUsedDrawingFields(eq(givenUserId));
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDtoList() {
        return List.of(
                createdUsedDrawingFieldDto(1L, "캐릭터 디자인", true),
                createdUsedDrawingFieldDto(2L, "메타버스", true),
                createdUsedDrawingFieldDto(3L, "일러스트", false),
                createdUsedDrawingFieldDto(4L, "게임삽화/원화", false)
        );
    }

    private UsedDrawingFieldDto createdUsedDrawingFieldDto(long drawingFieldId, String drawingFieldName, boolean used) {
        return UsedDrawingFieldDto.builder()
                .drawingFieldId(drawingFieldId)
                .drawingFieldName(drawingFieldName)
                .used(used)
                .build();
    }


}