package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.PortfolioRoomErrorCode;
import com.onfree.common.error.exception.PortfolioRoomException;
import com.onfree.core.dto.portfolioroom.UpdatePortfolioStatusDto;
import com.onfree.core.dto.portfolioroom.UpdateStatusMessageDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.PortfolioRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserPortfolioRoomController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtistUserPortfolioRoomControllerTest extends ControllerBaseTest {

    @MockBean
    PortfolioRoomService portfolioRoomService;

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT]상태메시지 변경하기")
    public void givenMessage_whenStatusMessageModify_thenSuccessMessage() throws Exception{
        //given
        ArtistUser artistUser = getArtistUser(1L);
        doNothing().when(portfolioRoomService)
                .modifyStatusMessage(any(ArtistUser.class), any());

        //when //then
        mvc.perform(put("/api/v1/artist/me/portfolio-room/status-message")
            .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getArtistAuthority())))
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            createUpdateStatusMessageDto("새로운 상태 메시지 입니다.")
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("상태메시지가 성공적으로 변경 되었습니다."))
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(portfolioRoomService).modifyStatusMessage(any(ArtistUser.class), any());
    }

    private List<SimpleGrantedAuthority> getArtistAuthority() {
        return List.of(new SimpleGrantedAuthority("ROLE_ARTIST"));
    }

    private ArtistUser getArtistUser(long userId) {
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
                .userId(userId)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon1@naver.com")
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

    private UpdateStatusMessageDto createUpdateStatusMessageDto(String message) {
        return UpdateStatusMessageDto.createUpdateStatusMessageDto(message);
    }



    @Test
    @DisplayName("[성공][PUT]포트폴리오룸 상태변경하기")
    public void givenPortfolioStatus_whenPortfolioRoomStatusModify_thenSuccessMessage() throws Exception{
        //given
        ArtistUser artistUser = getArtistUser(1L);
        doNothing().when(portfolioRoomService)
                .modifyPortfolioStatus(any(ArtistUser.class), any());

        //when //then
        mvc.perform(put("/api/v1/artist/me/portfolio-room/status")
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getArtistAuthority())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createUpdatePortfolioStatusDto(false)
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("포트폴리오룸 상태가 성공적으로 변경 되었습니다."))
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(portfolioRoomService).modifyPortfolioStatus(any(ArtistUser.class), any());
    }

    public UpdatePortfolioStatusDto createUpdatePortfolioStatusDto(boolean status){
        return UpdatePortfolioStatusDto.createUpdatePortfolioStatusDto(status);
    }

    @Test
    @DisplayName("[실페][PUT] 포트폴리오룸이 운영자에게 의해 제한 걸려 공개 비공개 설정 실패 - NOT_ACCESS_PORTFOLIO_ROOM")
    public void givenStatus_whenPortfolioStatusIsCanNotUsePortfolioRoom_thenNotAccessPortfolioRoomError() throws Exception{
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM;
        ArtistUser artistUser = getArtistUser(1L);

        doThrow(new PortfolioRoomException(PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM))
                .when(portfolioRoomService).modifyPortfolioStatus(any(), any());
        //when //then
        mvc.perform(put("/api/v1/artist/me/portfolio-room/status")
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getArtistAuthority())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createUpdatePortfolioStatusDto(false)
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioRoomService).modifyPortfolioStatus(any(), any());
    }
}