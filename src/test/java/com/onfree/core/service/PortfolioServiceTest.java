package com.onfree.core.service;

import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.exception.PortfolioException;
import com.onfree.core.dto.portfolio.CreatePortfolioContentDto;
import com.onfree.core.dto.portfolio.CreatePortfolioDto;
import com.onfree.core.dto.portfolio.PortfolioDetailDto;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.entity.DrawingField;
import com.onfree.core.entity.Portfolio;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.portfoliocontent.ImageContent;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.portfoliocontent.TextContent;
import com.onfree.core.entity.portfoliocontent.VideoContent;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.DrawingFieldRepository;
import com.onfree.core.repository.PortfolioRepository;
import com.onfree.core.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private DrawingFieldRepository drawingFieldRepository;
    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    @DisplayName("[성공] 포트폴리오 추가")
    public void givenUserIdAndCreatePortfolioDto_whenAddPortfolio_thenNotting() throws Exception{
        //given
        final long givenUserId = 1L;
        final boolean representative = false;
        final boolean temporary = false;

        when(userRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(getArtistUser())
                );
        when(drawingFieldRepository.findAllByDisabledIsFalseAndDrawingFieldIdIn(any()))
                .thenReturn(
                        getDrawingFields()
                );
        //when
        portfolioService.addPortfolio(givenUserId, givenCreatePortfolioDto("제목", "mainImageUrl", temporary));

        //then

        verify(userRepository).findById(eq(givenUserId));
        verify(portfolioRepository, never()).findByArtistUserAndDeletedIsFalse(any(ArtistUser.class));
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    private List<DrawingField> getDrawingFields() {
        return List.of(
                DrawingField.createDrawingField("캐릭터 디자인", "캐릭터 디자인", false),
                DrawingField.createDrawingField("일러스트", "일러스트", false),
                DrawingField.createDrawingField("메타버스", "메타버스", false)
        );
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
                .newsAgency("SKT")
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

    private CreatePortfolioDto.Request givenCreatePortfolioDto(String title, String mainImageUrl, boolean temporary) {
        final List<String> tags = List.of("일러스트", "캐릭터 작업");
        final List<CreatePortfolioContentDto> createPortfolioContentDtos = getCreatePortfolioContentDtos();
        return CreatePortfolioDto.Request.createPortfolioDto(title, mainImageUrl, tags, createPortfolioContentDtos,  temporary);
    }

    private List<CreatePortfolioContentDto> getCreatePortfolioContentDtos() {
        return List.of(
                CreatePortfolioContentDto.createImageContent("imageUrl"),
                CreatePortfolioContentDto.createTextContent("imageUrl"),
                CreatePortfolioContentDto.createVideoContent("imageUrl")
        );
    }

    private Portfolio createPortfolio(ArtistUser artistUser, String title, boolean representative, boolean temporary) {
        final String tags = String.join(", ", List.of("일러스트", "캐릭터 작업"));
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields();
        final List<PortfolioContent> portfolioContents = getPortfolioContents();
        return Portfolio.createPortfolio(
                artistUser, "mainImageUrl", title, portfolioContents,
                tags, portfolioDrawingFields, representative, temporary);
    }

    private List<PortfolioDrawingField> getPortfolioDrawingFields() {
        return List.of(
                createPortfolioDrawingField("캐릭터디자인", "캐릭터디자인", false),
                createPortfolioDrawingField("일러스트", "일러스트", false)
        );
    }

    private PortfolioDrawingField createPortfolioDrawingField(String fieldName, String description, boolean top) {
        return PortfolioDrawingField.createPortfolioDrawingField(
                DrawingField.createDrawingField(fieldName, description, top)
        );
    }

    private List<PortfolioContent> getPortfolioContents() {
        return List.of(
                VideoContent.createVideoContent("videoUrl"),
                ImageContent.createImageContent("imageUrl"),
                TextContent.createTextContent("text")
        );
    }

    @Test
    @DisplayName("[성공] 포트폴리오 상세 조회 - 임시 저장 글이 아닌 경우")
    public void givenPortfolioId_whenFindPortfolio_thenPortfolioDetailDto() throws Exception{
        //given

        final long givenPortfolioId = 1L;

        final Portfolio portfolio = getPortfolio(givenPortfolioId, false);
        final Long beforeView = portfolio.getView();

        when(portfolioRepository.findByPortfolioIdAndTemporaryAndDeletedIsFalse(anyLong(), eq(false)))
                .thenReturn(
                        Optional.of(
                                portfolio
                        )
                );
        //when //then
        final PortfolioDetailDto portfolioDetailDto = portfolioService.findPortfolio(givenPortfolioId, false);
        assertThat(beforeView + 1).isEqualTo(portfolio.getView())
                .as("조회 전과 후 조회수 증가 검증");
        assertThat(portfolioDetailDto)
            .hasFieldOrPropertyWithValue("portfolioId", givenPortfolioId)
            .hasFieldOrPropertyWithValue("artistUser", "joon@naver.com")
            .hasFieldOrPropertyWithValue("title", "제목")
            .hasFieldOrPropertyWithValue("representative", false)
            .hasFieldOrPropertyWithValue("temporary", false)
        ;

        verify(portfolioRepository).findByPortfolioIdAndTemporaryAndDeletedIsFalse(anyLong(), eq(false));

    }

    private Portfolio getPortfolio(long portfolioId, boolean temporary) {
        return Portfolio.builder()
                .portfolioId(portfolioId)
                .representative(false)
                .artistUser(getArtistUser())
                .temporary(temporary)
                .portfolioContents(List.of())
                .view(0L)
                .tags("일러스트,캐릭터작업")
                .portfolioDrawingFields(getPortfolioDrawingFields())
                .mainImageUrl("mainImageUrl")
                .title("제목")
                .build();
    }

    @Test
    @DisplayName("[성공] 포트폴리오 상세 조회 - 임시 저장 글인 경우")
    public void givenTemporaryPortfolioId_whenFindPortfolio_thenPortfolioDetailDto() throws Exception{
        //given
        final long givenTempPortfolioId = 1L;
        final Portfolio tempPortfolio = getPortfolio(givenTempPortfolioId, true);
        final Long beforeView = tempPortfolio.getView();

        when(portfolioRepository.findByPortfolioIdAndTemporaryAndDeletedIsFalse(anyLong(), eq(true)))
                .thenReturn(
                        Optional.of(
                                tempPortfolio
                        )
                );
        //when //then
        final PortfolioDetailDto portfolioDetailDto = portfolioService.findPortfolio(givenTempPortfolioId, true);
        assertThat(beforeView).isEqualTo(tempPortfolio.getView())
                .as("임시 저장글 조회 전과 후 조회수 동일");
        assertThat(portfolioDetailDto)
                .hasFieldOrPropertyWithValue("portfolioId", givenTempPortfolioId)
                .hasFieldOrPropertyWithValue("artistUser", "joon@naver.com")
                .hasFieldOrPropertyWithValue("title", "제목")
                .hasFieldOrPropertyWithValue("representative", false)
                .hasFieldOrPropertyWithValue("temporary", true)
        ;
        verify(portfolioRepository).findByPortfolioIdAndTemporaryAndDeletedIsFalse(eq(givenTempPortfolioId), eq(true));
    }

    @Test
    @DisplayName("[실패] 포트폴리오 상세 조회 - 포트폴리오가 없는 경우")
    public void givenNotFoundPortfolioId_whenFindPortfolio_thenNotfoundPortfolioError() throws Exception{
        //given
        final long givenTempPortfolioId = 1L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;

        when(portfolioRepository.findByPortfolioIdAndTemporaryAndDeletedIsFalse(anyLong(), eq(false)))
                .thenReturn(
                        Optional.empty()
                );
        //when //then
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.findPortfolio(givenTempPortfolioId, false)
        );
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findByPortfolioIdAndTemporaryAndDeletedIsFalse(eq(givenTempPortfolioId), eq(false));
    }


    @Test
    @DisplayName("[성공] 작가 포트폴리오 조회")
    public void givenUserIdAndTemporary_whenFindAllPortfolioByUserIdAndTemporary_thenPortfolioSimpleDtos() throws Exception{
        //given
        final long givenUserId = 1L;
        final long portfolioId = 1L;
        final boolean temporary = false;
        final ArtistUser artistUser = getArtistUser();

        when(userRepository.findById(givenUserId))
                .thenReturn(
                        Optional.ofNullable(
                                artistUser
                        )
                );

        when(portfolioRepository.findByArtistUserAndTemporaryAndDeletedIsFalse(any(ArtistUser.class), eq(false)))
                .thenReturn(
                        List.of(
                                createPortfolio(artistUser, "제목1", temporary, false),
                                createPortfolio(artistUser, "제목2", temporary, false),
                                createPortfolio(artistUser, "제목3", temporary, false)
                        )
                );
        //when
        final List<PortfolioSimpleDto> portfolioSimpleDtos
                = portfolioService.findAllPortfolioByUserIdAndTemporary(portfolioId, temporary);

        //then
        assertThat(portfolioSimpleDtos.size()).isEqualTo(3);
        assertThat(portfolioSimpleDtos.get(0))
                .hasFieldOrPropertyWithValue("mainImageUrl", "mainImageUrl")
                .hasFieldOrPropertyWithValue("title", "제목1")
                .hasFieldOrPropertyWithValue("view", 0L)
        ;
        verify(userRepository).findById(eq(givenUserId));
        verify(portfolioRepository).findByArtistUserAndTemporaryAndDeletedIsFalse(any(ArtistUser.class), eq(temporary));
    }

    @Test
    @DisplayName("[성공] 작가 임시 저장 포트폴리오 조회")
    public void givenUserIdAndTemporaryIsTrue_whenFindAllPortfolioByUserIdAndTemporary_thenPortfolioSimpleDtos() throws Exception{
        //given
        final long givenUserId = 1L;
        final long portfolioId = 1L;
        final boolean temporary = true;
        final ArtistUser artistUser = getArtistUser();

        when(userRepository.findById(givenUserId))
                .thenReturn(
                        Optional.ofNullable(
                                artistUser
                        )
                );

        when(portfolioRepository.findByArtistUserAndTemporaryAndDeletedIsFalse(any(ArtistUser.class), eq(true)))
                .thenReturn(
                        List.of(
                                createPortfolio(artistUser, "제목1", temporary, false),
                                createPortfolio(artistUser, "제목2", temporary, false),
                                createPortfolio(artistUser, "제목3", temporary, false)
                        )
                );
        //when
        final List<PortfolioSimpleDto> portfolioSimpleDtos
                = portfolioService.findAllPortfolioByUserIdAndTemporary(portfolioId, temporary);

        //then
        assertThat(portfolioSimpleDtos.size()).isEqualTo(3);
        assertThat(portfolioSimpleDtos.get(0))
                .hasFieldOrPropertyWithValue("mainImageUrl", "mainImageUrl")
                .hasFieldOrPropertyWithValue("title", "제목1")
                .hasFieldOrPropertyWithValue("view", 0L)
        ;
        verify(userRepository).findById(eq(givenUserId));
        verify(portfolioRepository).findByArtistUserAndTemporaryAndDeletedIsFalse(any(ArtistUser.class), eq(temporary));
    }

    @Test
    @DisplayName("[성공] 포트폴리오 삭제하기")
    public void givenPortfolioId_whenPortfolioRemove_thenSuccess() throws Exception{
        //given
        final long portfolioId = 1L;
        Long userId = 1L;
        final Portfolio portfolio = getPortfolio(portfolioId, true);
        final boolean beforeDeleted = portfolio.isDeleted();
        when(portfolioRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(
                                portfolio
                        )
                );
        //when
        portfolioService.removePortfolio(portfolioId, userId);

        //then
        assertAll(
                () -> assertThat(beforeDeleted).isFalse(),
                () -> assertThat(portfolio.isDeleted()).isTrue()
        );

        verify(portfolioRepository).findById(eq(portfolioId));
    }

    @Test
    @DisplayName("[실패] 포트폴리오 삭제하기 - 중복으로 삭제요청을 보낸 경우")
    public void givenPortfolioId_whenDeplicatedPortfolioRemove_thenAlreadyDeletedPortfolioError() throws Exception{
        //given
        final long portfolioId = 1L;
        Long userId = 1L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        when(portfolioRepository.findById(anyLong()))
                .thenThrow(
                        new PortfolioException(errorCode)
                );
        //when
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.removePortfolio(portfolioId, userId)
        );


        //then
         assertAll(
                 () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                 () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
         );
        verify(portfolioRepository).findById(eq(portfolioId));
    }

    @Test
    @DisplayName("[성공] 포트폴리오 대표 설정")
    public void givenPortfolioId_whenRepresentPortfolio_thenSuccess() throws Exception{
        //given
        final long portfolioId = 1L;
        Long userId = 1L;
        final Portfolio portfolio = getPortfolio(portfolioId, false);
        final boolean beforeRepresentative = portfolio.isRepresentative();
        final List<Portfolio> portfoliosAnyOneRepresentative = getPortfoliosAnyOneRepresentative();
        final boolean beforeHasRepresentative = portfoliosAnyOneRepresentative.stream().anyMatch(Portfolio::isRepresentative);
        when(portfolioRepository.findById(anyLong()))
                .thenReturn(
                        Optional.of(
                                portfolio
                        )
                );

        when(portfolioRepository.findByArtistUserAndDeletedIsFalse(any(ArtistUser.class)))
                .thenReturn(
                        portfoliosAnyOneRepresentative
                );

        //when
        portfolioService.representPortfolio(portfolioId, userId);

        //then
        assertAll(
                () -> assertThat(beforeRepresentative).isFalse(),
                () -> assertThat(portfolio.isRepresentative()).isTrue(),
                () -> assertThat(beforeHasRepresentative)
                        .isTrue().as("사용자 포트폴리오에 메소드 실행전 대표 포트폴리오가 있는 것을 확인"),
                () -> assertThat(
                        portfoliosAnyOneRepresentative.stream()
                        .anyMatch(Portfolio::isRepresentative)
                    ).isFalse().as("해당 메소드 실행 수 대표로 지정되었던 포트폴리오가 해제되었음")
        );
        verify(portfolioRepository).findById(eq(portfolioId));
        verify(portfolioRepository).findByArtistUserAndDeletedIsFalse(any(ArtistUser.class));
    }

    private List<Portfolio> getPortfoliosAnyOneRepresentative() {
        return List.of(
                createPortfolio(getArtistUser(), "제목1", false, false),
                createPortfolio(getArtistUser(), "제목2", false, false),
                createPortfolio(getArtistUser(), "대표 포트폴리오", true, false),
                createPortfolio(getArtistUser(), "제목4", false, false),
                createPortfolio(getArtistUser(), "제목5", false, false)
        );
    }

    @Test
    @DisplayName("[실패] 포트폴리오 대표 설정 - 해당 포트폴리오가 임시 저장 포트폴리오 인 경우")
    public void givenTempPortfolioId_whenRepresentPortfolio_thenPortfolioError() throws Exception{
        //given
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO;

        final long portfolioId = 1L;
        Long userId = 1L;
        when(portfolioRepository.findById(anyLong()))
            .thenReturn(
                    Optional.of(
                            getPortfolio(portfolioId, true)
                    )
            );
        //when
        final PortfolioException exception = assertThrows(PortfolioException.class,
            () -> portfolioService.representPortfolio(portfolioId, userId)
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findById(eq(portfolioId));
        verify(portfolioRepository, never()).findByArtistUserAndDeletedIsFalse(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[실패] 포트폴리오 대표 설정 - 해당 포트폴리오가 이미 대표 포트폴리오로 지정된 경우")
    public void givenPortfolioId_whenDuplicatedRepresentPortfolio_thenPortfolioError() throws Exception{
        //given
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_REGISTER_REPRESENTATIVE_PORTFOLIO;

        Long userId = 1L;
        final long portfolioId = 1L;

        when(portfolioRepository.findById(anyLong()))
                .thenThrow(
                        new PortfolioException(errorCode)
                );
        //when
        final PortfolioException exception = assertThrows(PortfolioException.class,
                () -> portfolioService.representPortfolio(portfolioId, userId)
        )
                ;
        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findById(eq(portfolioId));
        verify(portfolioRepository, never()).findByArtistUserAndDeletedIsFalse(any(ArtistUser.class));
    }

}