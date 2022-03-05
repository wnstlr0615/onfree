package com.onfree.controller;

import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.PortfolioRoomErrorCode;
import com.onfree.common.error.exception.PortfolioRoomException;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolioroom.PortfolioRoomDetailDto;
import com.onfree.core.entity.user.StatusMark;
import com.onfree.core.service.PortfolioRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PortfolioRoomController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PortfolioRoomControllerTest extends ControllerBaseTest {
    @MockBean
    PortfolioRoomService portfolioRoomService;

    @Test
    @DisplayName("[성공][GET] 포트폴리오룸 조회하기")
    public void givenPortfolioRoomURL_whenPortfolioRoomDetails_thenPortfolioRoomDetailDto() throws Exception{
        //given
        when(portfolioRoomService.findOnePortfolioRoom(anyString()))
                .thenReturn(
                        getPortfolioRoomDetailDto()
                );
        //when //then
        String portfolioRoomURL = "Joon";
        mvc.perform(get("/api/v1/portfolio-rooms/{portfolioRoomURL}", portfolioRoomURL)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.portFolioRoomId").value(1L))
            .andExpect(jsonPath("$.portfolioRoomURL").value("joon"))
            .andExpect(jsonPath("$.nickname").value("joon"))
            .andExpect(jsonPath("$.profileImage").value("http://onfree.io/images/123456789"))
            .andExpect(jsonPath("$.starPoint").value(0.))
            .andExpect(jsonPath("$.drawingFields").isNotEmpty())
            .andExpect(jsonPath("$.statusMark").isNotEmpty())
            .andExpect(jsonPath("$.portfolioRoomStatus").isNotEmpty())
            .andExpect(jsonPath("$.statusMessage").isNotEmpty())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(portfolioRoomService).findOnePortfolioRoom(anyString());
    }

    private PortfolioRoomDetailDto getPortfolioRoomDetailDto() {
        long portFolioRoomId = 1L;
        String portfolioRoomURL = "joon";
        List<UsedDrawingFieldDto> usedDrawingFieldDtos = List.of(
                UsedDrawingFieldDto.createUsedDrawingFieldDto("캐릭터 디자인", true),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("일러스트", false),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("메타버스", false)
        );
        boolean portfolioStatus = true;
        String statusMessage = "상태 메시지 입니다.";
        String nickname = "joon";
        String profileImage = "http://onfree.io/images/123456789";
        double starPoint = 0;
        return PortfolioRoomDetailDto.createPortfolioRoomDetailDto(portFolioRoomId, portfolioRoomURL, nickname, profileImage, starPoint, usedDrawingFieldDtos, StatusMark.OPEN, portfolioStatus, statusMessage);
    }

    @Test
    @DisplayName("[실패] 잘못된 주소로 포트폴리오룸에 접근한 경우 - NOT_FOUND_PORTFOLIO_ROOM")
    public void givenWrongPortfolioURL_whenPortfolioRoomDetails_thenNotFoundPortfolioRoomError() throws Exception{
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_FOUND_PORTFOLIO_ROOM;
        when(portfolioRoomService.findOnePortfolioRoom(anyString()))
                .thenThrow(new PortfolioRoomException(errorCode));
        //when //then
        String portfolioRoomURL = "wrongURL";
        mvc.perform(get("/api/v1/portfolio-rooms/{portfolioRoomURL}", portfolioRoomURL)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioRoomService).findOnePortfolioRoom(eq(portfolioRoomURL));
    }

    @Test
    @DisplayName("[실패] 비공개 포트폴리오룸에 접근한 경우 - PORTFOLIO_ROOM_IS_PRIVATE")
    public void givenPrivatePortfolioURL_whenPortfolioRoomDetails_thenPortfolioRoomIsPrivateError() throws Exception{
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.PORTFOLIO_ROOM_IS_PRIVATE;
        when(portfolioRoomService.findOnePortfolioRoom(anyString()))
                .thenThrow(new PortfolioRoomException(errorCode));
        //when //then
        String portfolioRoomURL = "privateRoomURL";
        mvc.perform(get("/api/v1/portfolio-rooms/{portfolioRoomURL}", portfolioRoomURL)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioRoomService).findOnePortfolioRoom(eq(portfolioRoomURL));
    }

    @Test
    @DisplayName("[실패] 비공개 포트폴리오룸에 접근한 경우 - NOT_ACCESS_PORTFOLIO_ROOM")
    public void givenCanNotUsePortfolioURL_whenPortfolioRoomDetails_theNotAccessPortfolioRoomError() throws Exception{
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM;
        when(portfolioRoomService.findOnePortfolioRoom(anyString()))
                .thenThrow(new PortfolioRoomException(errorCode));
        //when //then
        String portfolioRoomURL = "joon";
        mvc.perform(get("/api/v1/portfolio-rooms/{portfolioRoomURL}", portfolioRoomURL)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioRoomService).findOnePortfolioRoom(eq(portfolioRoomURL));
    }

}