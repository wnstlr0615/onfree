package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.WebMvcBaseTest;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.error.exception.PortfolioException;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolio.*;
import com.onfree.core.service.PortfolioService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;

import static com.onfree.core.dto.portfolio.CreatePortfolioContentDto.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PortfolioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PortfolioControllerTest extends WebMvcBaseTest {
    @MockBean
    PortfolioService portfolioService;

    @Test
    @WithArtistUser
    @DisplayName("[성공][POST] 포트폴리오 작성하기")
    public void givenCratedPortfolioDto_whenPortfolioAdd_thenSimpleResponse() throws Exception {
        //given
        final long givenUserId = 1L;
        final CreatePortfolioDto.Request createPortfolioDto = givenCreatedPortfolioDto("포트폴리오 제목", false, false);

        doNothing().when(portfolioService)
                .addPortfolio(anyLong(), any(CreatePortfolioDto.Request.class));
        when(checker.isSelf(anyLong())).thenReturn(true);
        //when //then
        mvc.perform(post("/api/portfolios")
                .queryParam("userId", String.valueOf(givenUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createPortfolioDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("포트폴리오가 성공적으로 등록되었습니다."))
        ;
        verify(portfolioService).addPortfolio(eq(givenUserId), any(CreatePortfolioDto.Request.class));
    }

    private CreatePortfolioDto.Request givenCreatedPortfolioDto(String title, boolean representative, boolean temporary) {
        List<CreatePortfolioContentDto> createPortfolioContentDtoList = new ArrayList<>();
        createPortfolioContentDtoList.add(createTextContent("텍스트"));
        createPortfolioContentDtoList.add(createVideoContent("https://www.youtube.com/watch?v=vSY0VEuqeRo&t=146s"));
        createPortfolioContentDtoList.add(createImageContent("https://onfree-store.s3.ap-northeast-2.amazonaws.com/13123.PNG"));

        return CreatePortfolioDto.Request.createPortfolioDto(
                title,
                "https://onfree-store.s3.ap-northeast-2.amazonaws.com/13123.PNG",
                List.of("일러스트", "캐릭터"),
                createPortfolioContentDtoList,
                temporary
        );
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][POST] 포트폴리오 작성하기 - 다른 사용자가 접근 할 경우")
    public void givenOtherUserAndCratedPortfolioDto_whenPortfolioAdd_thenAccessDeniedError() throws Exception {
        //given
        final long givenUserId = 1L;
        final CreatePortfolioDto.Request createPortfolioDto = givenCreatedPortfolioDto("포트폴리오 제목", false, false);
        final GlobalErrorCode errorCode = GlobalErrorCode.ACCESS_DENIED;

        doNothing().when(portfolioService)
                .addPortfolio(anyLong(), any(CreatePortfolioDto.Request.class));
        when(checker.isSelf(anyLong())).thenThrow(new GlobalException(errorCode));
        //when //then
        mvc.perform(post("/api/portfolios")
                .queryParam("userId", String.valueOf(givenUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createPortfolioDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioService, never()).addPortfolio(eq(givenUserId), any(CreatePortfolioDto.Request.class));
    }

    @Test
    @DisplayName("[성공][GET]포트폴리오 상세 조회")
    public void givenPortfolioId_whenPortfolioDetails_thenSuccess() throws Exception {
        //given
        final long givenPortfolioId = 1L;
        final String artistUser = "joon@naver.com";
        final String title = "제목입니다";
        final boolean temporary = false;
        final boolean representative = false;

        when(portfolioService.findPortfolio(
                eq(givenPortfolioId), eq(false)
        )).thenReturn(
                createPortfolioDetailDto(givenPortfolioId, artistUser, title, temporary, representative)
        );

        //when

        //then
        mvc.perform(get("/api/portfolios/{portfolioId}", givenPortfolioId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.portfolioId").value(givenPortfolioId))
                .andExpect(jsonPath("$.data.artistUser").value(artistUser))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.representative").value(representative))
                .andExpect(jsonPath("$.data.temporary").value(temporary))
                .andExpect(jsonPath("$.data.tags[0]").value("일러스트"))
                .andExpect(jsonPath("$.data.contents").isNotEmpty())
        ;
        verify(portfolioService).findPortfolio(eq(givenPortfolioId), eq(false));
    }

    private PortfolioDetailDto createPortfolioDetailDto(long portfolioId, String artistUser, String title, boolean temporary, boolean representative) {
        final List<String> tags = getTags();
        final List<PortfolioContentDetailDto> contents = List.of(
                PortfolioContentDetailDto.createImagePortfolioContentDetailDto("imageUrl"),
                PortfolioContentDetailDto.createVideoPortfolioContentDetailDto("videoUrl"),
                PortfolioContentDetailDto.createTextPortfolioContentDetailDto("text")
        );
        final List<UsedDrawingFieldDto> usedDrawingFieldDtos = List.of(
                UsedDrawingFieldDto.createUsedDrawingFieldDto("캐릭터 디자인", false),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("일러스트", true),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("버츄얼 디자인", false),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("메타 버스", true)
        );
        return PortfolioDetailDto
                .createPortfolioDetailDto(portfolioId, artistUser, title, representative, temporary, tags, contents, usedDrawingFieldDtos);
    }

    private List<String> getTags() {
        return List.of("일러스트");
    }

    @Test
    @DisplayName("[실패][GET]포트폴리오 상세 조회 - 해당 포트폴리오가 없는 경우")
    public void givenTemporaryPortfolioId_whenPortfolioDetails_thenAccessDeniedError() throws Exception {
        //given
        final long givenPortfolioId = 1L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;

        when(portfolioService.findPortfolio(
                eq(givenPortfolioId), anyBoolean()
        )).thenThrow(
                 new PortfolioException(errorCode)
        );
        //when

        //then
        mvc.perform(get("/api/portfolios/{portfolioId}", givenPortfolioId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(portfolioService).findPortfolio(eq(givenPortfolioId), eq(false));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET]임시 저장 포트폴리오 상세 조회")
    public void givenTempPortfolioId_whenPortfolioDetails_thenSuccess() throws Exception {
        //given
        final long givenTempPortfolioId = 1L;
        final long userId = 1L;
        final String artistUser = "joon@naver.com";
        final String title = "제목입니다";
        final boolean temporary = true;
        final boolean representative = false;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);

        when(portfolioService.findPortfolio(
                eq(givenTempPortfolioId), eq(true)
        )).thenReturn(
                createPortfolioDetailDto(givenTempPortfolioId, artistUser, title, temporary, representative)
        );

        //when //then
        mvc.perform(get("/api/portfolios/{portfolioId}/temp", givenTempPortfolioId)
                .queryParam("userId", String.valueOf(userId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.portfolioId").value(givenTempPortfolioId))
                .andExpect(jsonPath("$.data.artistUser").value(artistUser))
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.representative").value(representative))
                .andExpect(jsonPath("$.data.temporary").value(temporary))
                .andExpect(jsonPath("$.data.tags[0]").value("일러스트"))
                .andExpect(jsonPath("$.data.contents").isNotEmpty())
        ;
        verify(portfolioService).findPortfolio(eq(givenTempPortfolioId), eq(true));
    }

    @Test
    @WithMockUser
    @DisplayName("[실패][GET]임시 저장 포트폴리오 상세 조회 - 다른 사용자가 접근할 경우")
    public void givenTempPortfolioId_whenPortfolioDetailsButOtherUserAccess_thenFail() throws Exception {
        //given
        final long givenTempPortfolioId = 1L;
        final long userId = 1L;
        final String artistUser = "joon@naver.com";
        final String title = "제목입니다";
        final boolean temporary = true;
        final boolean representative = false;
        ErrorCode errorCode = GlobalErrorCode.ACCESS_DENIED;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);

        when(portfolioService.findPortfolio(
                eq(givenTempPortfolioId), eq(true)
        )).thenReturn(
                createPortfolioDetailDto(givenTempPortfolioId, artistUser, title, temporary, representative)
        );

        //when //then
        mvc.perform(get("/api/portfolios/{portfolioId}/temp", givenTempPortfolioId)
                .queryParam("userId", String.valueOf(userId))
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(portfolioService, never()).findPortfolio(eq(givenTempPortfolioId), eq(true));
    }



    @Test
    @DisplayName("[성공][GET]포트폴리오 전체 조회")
    public void givenUserIdAndTemporary_whenPortfolioDetailListThenSuccess() throws Exception{
        //given
        final long givenUserId = 1L;
        final boolean temporary = false;

        when(portfolioService.findAllPortfolioByUserIdAndTemporary(anyLong(), anyBoolean()))
                .thenReturn(
                        getPortfolioSimpleDtos()
                );
        //when
        //then
        mvc.perform(get("/api/portfolios")
                .queryParam("userId", String.valueOf(givenUserId))
                .queryParam("temporary", String.valueOf(temporary))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data[0].portfolioId").value(1L))
            .andExpect(jsonPath("$.data[0].mainImageUrl").value("mainImageUrl"))
            .andExpect(jsonPath("$.data[0].title").value("제목1"))
            .andExpect(jsonPath("$.data[0].view").value(0))
        ;
        verify(checker, never()).isSelf(anyLong());
        verify(portfolioService).findAllPortfolioByUserIdAndTemporary(eq(givenUserId), eq(temporary));
    }

    private List<PortfolioSimpleDto> getPortfolioSimpleDtos() {
        return List.of(
                createPortfolioSimple(1L, "제목1", "mainImageUrl"),
                createPortfolioSimple(2L, "제목2", "mainImageUrl"),
                createPortfolioSimple(3L, "제목3", "mainImageUrl")
        );
    }

    private PortfolioSimpleDto createPortfolioSimple(long portfolioId, String title, String mainImageUrl) {
        return PortfolioSimpleDto.createPortfolioSimpleDto(portfolioId, title, mainImageUrl, false);
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET]포트폴리오 임시 저장글 전체 조회 - 작성자 본인이 일경우")
    public void givenUserIdAndTemporary_whenPortfolioDetailListThenAccessDenied() throws Exception{
        //given
        final long givenUserId = 1L;
        final boolean temporary = true;


        when(portfolioService.findAllPortfolioByUserIdAndTemporary(anyLong(), anyBoolean()))
                .thenReturn(
                        getPortfolioSimpleDtos()
                );

        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when
        //then
        mvc.perform(get("/api/portfolios")
                .queryParam("userId", String.valueOf(givenUserId))
                .queryParam("temporary", String.valueOf(temporary))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].portfolioId").value(1L))
                .andExpect(jsonPath("$.data[0].mainImageUrl").value("mainImageUrl"))
                .andExpect(jsonPath("$.data[0].title").value("제목1"))
                .andExpect(jsonPath("$.data[0].view").value(0))
        ;
        verify(checker).isSelf(anyLong());
        verify(portfolioService).findAllPortfolioByUserIdAndTemporary(eq(givenUserId), eq(temporary));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][GET]포트폴리오 임시 저장글 전체 조회 - 작성자 본인이 아닌 경우")
    public void givenOtherUserIdAndTemporary_whenPortfolioDetailListThenAccessDenied() throws Exception{
        //given
        final long givenUserId = 1L;
        final boolean temporary = true;
        final GlobalErrorCode errorCode = GlobalErrorCode.ACCESS_DENIED;

        when(checker.isSelf(anyLong()))
                .thenThrow(new GlobalException(errorCode));
        //when
        //then
        mvc.perform(get("/api/portfolios")
                .queryParam("userId", String.valueOf(givenUserId))
                .queryParam("temporary", String.valueOf(temporary))
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(checker).isSelf(anyLong());
        verify(portfolioService, never()).findAllPortfolioByUserIdAndTemporary(eq(givenUserId), eq(temporary));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공] 포트폴리오 삭제")
    public void givenPortfolioId_whenRemovePortfolio_thenSuccess() throws Exception{
        //given
        final long givenPortfolioId = 1L;
        final long userId = 1L;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        doNothing().when(portfolioService)
                .removePortfolio(anyLong(), anyLong());
        //when
        //then
        mvc.perform(delete("/api/portfolios/{portfolioId}", givenPortfolioId)
                .queryParam("userId", String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("포트폴리오를 성공적으로 삭제하였습니다."))
        ;
        verify(checker).isSelf(eq(userId));
        verify(portfolioService).removePortfolio(eq(givenPortfolioId), eq(userId));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패] 포트폴리오 삭제 - 이미 삭제도니 포트폴리오 삭제 요청")
    public void givenPortfolioId_whenDuplicatedRemovePortfolio_thenAlreadyDeletePortfolioError() throws Exception{
        //given
        final long givenPortfolioId = 1L;
        final long userId = 1L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .removePortfolio(anyLong(), anyLong());
        //when
        //then
        mvc.perform(delete("/api/portfolios/{portfolioId}", givenPortfolioId)
                .queryParam("userId", String.valueOf(userId))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(checker).isSelf(eq(userId));
        verify(portfolioService).removePortfolio(eq(givenPortfolioId), eq(userId));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 대표 포트폴리오 설정")
    public void givenPortfolioId_whenPortfolioRepresent_themSuccess() throws Exception{
        //given
        final long portfolioId = 1L;
        final String userId = "1";
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        doNothing().when(portfolioService)
                .representPortfolio(anyLong(), anyLong());

        //when //then
        mvc.perform(put("/api/portfolios/{portfolioId}/representative", portfolioId)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("userId", userId)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("해당 포트폴리오를 대표설정하였습니다."))
        ;

        verify(portfolioService).representPortfolio(eq(portfolioId), eq(1L));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 대표 포트폴리오 설정 -  해당 포트폴리오가 임시 저장 포트폴리오인 경우")
    public void givenTempPortfolioId_whenPortfolioRepresent_thenPortfolioError() throws Exception{
        //given
        final long tempPortfolioId = 1L;
        final String userId = "1";

        PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .representPortfolio(anyLong(), anyLong());
        //when //then
        mvc.perform(put("/api/portfolios/{portfolioId}/representative", tempPortfolioId)
            .contentType(MediaType.APPLICATION_JSON)
                .queryParam("userId", userId)

        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(portfolioService).representPortfolio(eq(tempPortfolioId), eq(1L));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 대표 포트폴리오 설정 -  해당 포트폴리오를 이미 대표로 설정한 경우")
    public void givenPortfolioId_whenDuplicatedPortfolioRepresent_thenPortfolioError() throws Exception{
        //given
        final long tempPortfolioId = 1L;
        final String userId = "1";
        PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .representPortfolio(anyLong(), anyLong());
        //when //then
        mvc.perform(put("/api/portfolios/{portfolioId}/representative", tempPortfolioId)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("userId", userId)

        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(portfolioService).representPortfolio(eq(tempPortfolioId), eq(1L));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT]포토플리오 수정하기")
    public void givenPortfolioId_whenPortfolioUpdate_Success() throws Exception{
        //given
        final long portfolioId = 1L;
        final long userId = 1L;

        doNothing().when(portfolioService)
                .updatePortfolio(anyLong(), anyLong(), any(UpdatePortfolioDto.class));
        when(checker.isSelf(anyLong()))
                .thenReturn(true);
        //when //then

        mvc.perform(put("/api/portfolios/{portfolioId}", portfolioId)
            .queryParam("userId", String.valueOf(userId))
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                    mapper.writeValueAsString(
                            createUpdatePortfolioDto()
                    )
            )
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("포트폴리오 수정을 완료 하였습니다."))
        ;
        verify(portfolioService).updatePortfolio(eq(portfolioId), eq(userId), any(UpdatePortfolioDto.class));
    }

    private UpdatePortfolioDto createUpdatePortfolioDto() {
        final List<String> tags = getTags();
        final List<Long> drawingFieldIds = List.of(1L, 2L);
        final List<CreatePortfolioContentDto> contents =  List.of(
                CreatePortfolioContentDto.createTextContent("수정된 내용"),
                CreatePortfolioContentDto.createVideoContent("update-video-url"),
                CreatePortfolioContentDto.createImageContent("update-image-url")
        );

        return UpdatePortfolioDto.createUpdatePortfolioDto("mainImageUrl", "수정된 제목", tags, drawingFieldIds, false, contents);
    }

    @Test
    @Disabled("userId 제거 방향 고려")
    @DisplayName("[실패][PUT] 포트폴리오 수정하기 - 다른 사용자가 접근한 경우")
    public void givenOtherUserId_whenPortfolioUpdate_thenNotAccessPortfolioError() throws Exception{
        //given
        PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_ACCESS_PORTFOLIO;
        final long portfolioId = 1L;
        final long otherUserId = 1L;
        when(checker.isSelf(anyLong()))
                .thenReturn(false);
        //when //then
        mvc.perform(put("/api/portfolios/{portfolioId}", portfolioId)
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam("userId", String.valueOf(otherUserId))
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
    }
}