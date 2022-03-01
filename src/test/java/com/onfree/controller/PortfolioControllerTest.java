package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.exception.PortfolioException;
import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolio.*;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.PortfolioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
class PortfolioControllerTest extends ControllerBaseTest {
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    CurrentArtistUserArgumentResolver currentArtistUserArgumentResolver;

    @Test
    @WithArtistUser
    @DisplayName("[성공][POST] 정상적인 포트폴리오 작성하기")
    public void givenCratedPortfolioDtoRequest_whenPortfolioAdd_thenReturnCreatedPortfolioDtoResponse() throws Exception {
        //given
        final boolean temporary = false;
        final CreatePortfolioDto.Request createPortfolioDto
                = givenCreatedPortfolioDto("포트폴리오 제목", temporary);

        when(portfolioService.addPortfolio(any(ArtistUser.class), any(CreatePortfolioDto.Request.class)))
                .thenReturn(
                        getCreateNormalPortfolioDtoResponse()
                );

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getArtistUser());
        //when //then
        mvc.perform(post("/api/v1/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createPortfolioDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.portfolioId").value(1L))
                .andExpect(jsonPath("$.title").value("포트폴리오 제목"))
                .andExpect(jsonPath("$.status").value(PortfolioStatus.NORMAL.toString()))
                .andExpect(jsonPath("$.mainImageUrl").value("mainImageUrl"))
                .andExpect(jsonPath("$.drawingFields").isNotEmpty())
                .andExpect(jsonPath("$.tags").isNotEmpty())
        ;
        verify(portfolioService).addPortfolio(any(ArtistUser.class), any(CreatePortfolioDto.Request.class));
    }

    private ArtistUser getArtistUser() {
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK_BANK)
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
    private CreatePortfolioDto.Response getCreateNormalPortfolioDtoResponse() {
        final List<String> drawingFieldNames = List.of("캐릭터 디자인", "일러스트");
        final List<String> tags = getTags();
        final String mainImageUrl = "mainImageUrl";
        final long portfolioId = 1L;
        final String title = "포트폴리오 제목";
        final List<PortfolioContentDetailDto> contents = List.of(
                PortfolioContentDetailDto.createImagePortfolioContentDetailDto("imageUrl"),
                PortfolioContentDetailDto.createVideoPortfolioContentDetailDto("videoUrl"),
                PortfolioContentDetailDto.createTextPortfolioContentDetailDto("text")
        );

        return CreatePortfolioDto.Response.createPortfolioDtoResponse(
                portfolioId, title, contents,
                mainImageUrl, tags, drawingFieldNames, PortfolioStatus.NORMAL
        );
    }

    private CreatePortfolioDto.Request givenCreatedPortfolioDto(String title, boolean temporary) {
        List<CreatePortfolioContentDto> createPortfolioContentDtoList = getCreatePortfolioContentDtos();

        final List<Long> drawingfieldIds = List.of(1L,2L,3L);
        return CreatePortfolioDto.Request.createPortfolioDtoRequest(
                title,
                "https://onfree-store.s3.ap-northeast-2.amazonaws.com/13123.PNG",
                List.of("일러스트", "캐릭터"),
                createPortfolioContentDtoList,
                drawingfieldIds,
                temporary
        );
    }



    @Test
    @WithArtistUser
    @DisplayName("[성공][POST] 임시 저장 포트폴리오 작성하기")
    public void givenTempCratedPortfolioDtoRequest_whenPortfolioAdd_thenReturnTempCreatedPortfolioDtoResponse() throws Exception {
        //given
        final CreatePortfolioDto.Request createPortfolioDto
                = givenTempCreatedPortfolioDto("포트폴리오 제목");

        when(portfolioService.addPortfolio(any(ArtistUser.class), any(CreatePortfolioDto.Request.class)))
                .thenReturn(
                        getCreateTempPortfolioDtoResponse()
                );

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(getArtistUser());
        //when //then
        mvc.perform(post("/api/v1/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                createPortfolioDto
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.portfolioId").value(1L))
                .andExpect(jsonPath("$.title").value("포트폴리오 제목"))
                .andExpect(jsonPath("$.status").value(PortfolioStatus.TEMPORARY.toString()))
                .andExpect(jsonPath("$.mainImageUrl").value("defaultMainImage"))
                .andExpect(jsonPath("$.drawingFields").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty())
        ;
        verify(portfolioService).addPortfolio(any(ArtistUser.class), any(CreatePortfolioDto.Request.class));
    }

    private CreatePortfolioDto.Request givenTempCreatedPortfolioDto(String title) {
        List<CreatePortfolioContentDto> createPortfolioContentDtoList = getCreatePortfolioContentDtos();

        return CreatePortfolioDto.Request.createPortfolioDtoRequest(
                title,
                null,
                List.of(),
                createPortfolioContentDtoList,
                List.of(),
                true
        );
    }

    private CreatePortfolioDto.Response getCreateTempPortfolioDtoResponse() {
        final List<String> drawingFieldNames = List.of();
        final List<String> tags = List.of();
        final String mainImageUrl = "defaultMainImage";
        final long portfolioId = 1L;
        final String title = "포트폴리오 제목";
        final List<PortfolioContentDetailDto> contents = List.of(
                PortfolioContentDetailDto.createImagePortfolioContentDetailDto("imageUrl"),
                PortfolioContentDetailDto.createVideoPortfolioContentDetailDto("videoUrl"),
                PortfolioContentDetailDto.createTextPortfolioContentDetailDto("text")
        );

        return CreatePortfolioDto.Response.createPortfolioDtoResponse(
                portfolioId, title, contents,
                mainImageUrl, tags, drawingFieldNames, PortfolioStatus.TEMPORARY
        );
    }

    private List<CreatePortfolioContentDto> getCreatePortfolioContentDtos() {
        List<CreatePortfolioContentDto> createPortfolioContentDtoList = new ArrayList<>();
        createPortfolioContentDtoList.add(createTextContent("텍스트"));
        createPortfolioContentDtoList.add(createVideoContent("https://www.youtube.com/watch?v=vSY0VEuqeRo&t=146s"));
        createPortfolioContentDtoList.add(createImageContent("https://onfree-store.s3.ap-northeast-2.amazonaws.com/13123.PNG"));
        return createPortfolioContentDtoList;
    }

    @Test
    @DisplayName("[성공][GET]포트폴리오 상세 조회")
    public void givenPortfolioId_whenPortfolioDetails_thenSuccess() throws Exception {
        //given
        final long givenPortfolioId = 1L;
        final String email = "joon@naver.com";
        final String title = "제목입니다";


        when(portfolioService.findPortfolio(
                eq(givenPortfolioId)
        )).thenReturn(
                createPortfolioDetailDto(givenPortfolioId, email, title, PortfolioStatus.NORMAL)
        );

        //when

        //then
        mvc.perform(get("/api/v1/portfolios/{portfolioId}", givenPortfolioId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioId").value(givenPortfolioId))
                .andExpect(jsonPath("$.artistUser").value(email))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.tags[0]").value("일러스트"))
                .andExpect(jsonPath("$.contents").isNotEmpty())
        ;
        verify(portfolioService).findPortfolio(eq(givenPortfolioId));
    }

    private PortfolioDetailDto createPortfolioDetailDto(long portfolioId, String artistUser, String title, PortfolioStatus status) {
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
                .createPortfolioDetailDto(portfolioId, artistUser, title, status, tags, contents, usedDrawingFieldDtos);
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
                eq(givenPortfolioId))
        ).thenThrow(
                 new PortfolioException(errorCode)
        );
        //when

        //then
        mvc.perform(get("/api/v1/portfolios/{portfolioId}", givenPortfolioId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(portfolioService).findPortfolio(eq(givenPortfolioId));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET]임시 저장 포트폴리오 상세 조회")
    public void givenTempPortfolioId_whenPortfolioDetails_thenSuccess() throws Exception {
        //given
        final long givenTempPortfolioId = 1L;
        final String email = "joon@naver.com";
        final String title = "제목입니다";


        when(portfolioService.findTempPortfolio(eq(givenTempPortfolioId), any(ArtistUser.class))
        ).thenReturn(
                createPortfolioDetailDto(givenTempPortfolioId, email, title, PortfolioStatus.TEMPORARY)
        );

        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());

        //when //then
        mvc.perform(get("/api/v1/portfolios/{portfolioId}/temp", givenTempPortfolioId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioId").value(givenTempPortfolioId))
                .andExpect(jsonPath("$.artistUser").value(email))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.status").value(PortfolioStatus.TEMPORARY.toString()))
                .andExpect(jsonPath("$.tags[0]").value("일러스트"))
                .andExpect(jsonPath("$.contents").isNotEmpty())
        ;
        verify(portfolioService).findTempPortfolio(eq(givenTempPortfolioId), any(ArtistUser.class));
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

        when(portfolioService.findPortfolio(
                eq(givenTempPortfolioId)
        )).thenReturn(
                createPortfolioDetailDto(givenTempPortfolioId, artistUser, title, PortfolioStatus.TEMPORARY)
        );

        //when //then
        mvc.perform(get("/api/v1/portfolios/{portfolioId}/temp", givenTempPortfolioId)
                .queryParam("userId", String.valueOf(userId))
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(portfolioService, never()).findPortfolio(eq(givenTempPortfolioId));
    }

    private List<PortfolioSimpleDto> getPortfolioSimpleDtos() {
        return List.of(
                createPortfolioSimple(1L, "제목1", "mainImageUrl"),
                createPortfolioSimple(2L, "제목2", "mainImageUrl"),
                createPortfolioSimple(3L, "제목3", "mainImageUrl")
        );
    }

    private PortfolioSimpleDto createPortfolioSimple(long portfolioId, String title, String mainImageUrl) {
        return PortfolioSimpleDto.createPortfolioSimpleDto(portfolioId, title, mainImageUrl, PortfolioStatus.NORMAL);
    }




    @Test
    @WithArtistUser
    @DisplayName("[성공] 포트폴리오 삭제")
    public void givenPortfolioId_whenRemovePortfolio_thenNothing() throws Exception{
        //given
        final long givenPortfolioId = 1L;

        doNothing().when(portfolioService)
                .removePortfolio(anyLong(), any(ArtistUser.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());
        //when
        //then
        mvc.perform(delete("/api/v1/portfolios/{portfolioId}", givenPortfolioId)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("포트폴리오를 성공적으로 삭제하였습니다."))
        ;
        verify(portfolioService).removePortfolio(eq(givenPortfolioId), any(ArtistUser.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패] 포트폴리오 삭제 - 이미 삭제도니 포트폴리오 삭제 요청")
    public void givenPortfolioId_whenDuplicatedRemovePortfolio_thenAlreadyDeletePortfolioError() throws Exception{
        //given
        final long givenPortfolioId = 1L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .removePortfolio(anyLong(), any(ArtistUser.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());
        //when
        //then
        mvc.perform(delete("/api/v1/portfolios/{portfolioId}", givenPortfolioId)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(portfolioService).removePortfolio(eq(givenPortfolioId), any(ArtistUser.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT] 대표 포트폴리오 설정")
    public void givenPortfolioId_whenPortfolioRepresent_themSuccess() throws Exception{
        //given
        final long portfolioId = 1L;

        doNothing().when(portfolioService)
                .representPortfolio(anyLong(), any(ArtistUser.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());

        //when //then
        mvc.perform(put("/api/v1/portfolios/{portfolioId}/representative", portfolioId)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("해당 포트폴리오를 대표설정하였습니다."))
        ;

        verify(portfolioService).representPortfolio(eq(portfolioId), any(ArtistUser.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 대표 포트폴리오 설정 -  해당 포트폴리오가 임시 저장 포트폴리오인 경우")
    public void givenTempPortfolioId_whenPortfolioRepresent_thenPortfolioError() throws Exception{
        //given
        final long tempPortfolioId = 1L;

        PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO;

        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .representPortfolio(anyLong(), any(ArtistUser.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());
        //when //then
        mvc.perform(put("/api/v1/portfolios/{portfolioId}/representative", tempPortfolioId)
            .contentType(MediaType.APPLICATION_JSON)

        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
            .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(portfolioService).representPortfolio(eq(tempPortfolioId), any(ArtistUser.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 대표 포트폴리오 설정 -  해당 포트폴리오를 이미 대표로 설정한 경우")
    public void givenPortfolioId_whenDuplicatedPortfolioRepresent_thenPortfolioError() throws Exception{
        //given
        final long tempPortfolioId = 1L;
        PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        doThrow(new PortfolioException(errorCode)).when(portfolioService)
                .representPortfolio(anyLong(), any(ArtistUser.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());
        //when //then
        mvc.perform(put("/api/v1/portfolios/{portfolioId}/representative", tempPortfolioId)
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;

        verify(portfolioService).representPortfolio(eq(tempPortfolioId), any(ArtistUser.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][PUT]포토플리오 수정하기")
    public void givenPortfolioId_whenPortfolioUpdate_Success() throws Exception{
        //given
        final long portfolioId = 1L;

        doNothing().when(portfolioService)
                .updatePortfolio(anyLong(), any(ArtistUser.class), any(UpdatePortfolioDto.class));
        when(currentArtistUserArgumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(getArtistUser());

        //when //then

        mvc.perform(put("/api/v1/portfolios/{portfolioId}", portfolioId)
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
        verify(portfolioService).updatePortfolio(eq(portfolioId), any(ArtistUser.class), any(UpdatePortfolioDto.class));
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

}