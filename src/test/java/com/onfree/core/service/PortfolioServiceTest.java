package com.onfree.core.service;

import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.exception.PortfolioException;
import com.onfree.core.dto.portfolio.CreatePortfolioContentDto;
import com.onfree.core.dto.portfolio.CreatePortfolioDto;
import com.onfree.core.dto.portfolio.PortfolioDetailDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.portfoliocontent.ImageContent;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.portfoliocontent.TextContent;
import com.onfree.core.entity.portfoliocontent.VideoContent;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.DrawingFieldRepository;
import com.onfree.core.repository.PortfolioRepository;
import com.onfree.core.repository.UserRepository;
import com.onfree.core.service.portfolio.PortfolioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
    @DisplayName("[??????] ??????????????? ??????")
    public void givenNormalRequestDto_whenAddPortfolio_thenCreatePortfolioDtoResponse(){
        //given
        final boolean temporary = false;

        when(drawingFieldRepository.findAllByStatusNotDisabledAndTempDrawingFieldIdIn(any()))
                .thenReturn(
                        getDrawingFields()
                );

        when(portfolioRepository.save(any(Portfolio.class)))
                .thenReturn(
                        getPortfolioOnPortfolioStatus(PortfolioStatus.NORMAL)
                );
        //when
        final CreatePortfolioDto.Response response
                = portfolioService.addPortfolio(
                        getArtistUser(), givenNormalCreatePortfolioDtoRequest("??????", "mainImageUrl")
        );

        //then
        assertAll(
            () -> assertThat(response)
                    .hasFieldOrPropertyWithValue("portfolioId", 1L)
                    .hasFieldOrPropertyWithValue("mainImageUrl", "mainImageUrl")
                    .hasFieldOrPropertyWithValue("title", "??????")
                    .hasFieldOrPropertyWithValue("status", PortfolioStatus.NORMAL),
            () -> assertThat(response.getContents()).isNotEmpty(),
            () -> assertThat(response.getDrawingFields()).isNotEmpty(),
            () -> assertThat(response.getTags()).isNotEmpty()
        );
        verify(drawingFieldRepository).findAllByStatusNotDisabledAndTempDrawingFieldIdIn(any());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    private List<DrawingField> getDrawingFields() {
        return List.of(
                DrawingField.createDrawingField("????????? ?????????", "????????? ?????????", DrawingFieldStatus.USED),
                DrawingField.createDrawingField("????????????", "????????????", DrawingFieldStatus.USED),
                DrawingField.createDrawingField("????????????", "????????????", DrawingFieldStatus.USED)
        );
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

    private CreatePortfolioDto.Request givenNormalCreatePortfolioDtoRequest(String title, String mainImageUrl) {
        final List<String> tags = getTags();
        return createPortfolioDtoRequest(title, mainImageUrl, List.of(1L, 2L), tags, false);
    }

    private CreatePortfolioDto.Request createPortfolioDtoRequest(String title, String mainImageUrl, List<Long> drawingFieldIds, List<String> tags, boolean temporary) {
        final List<CreatePortfolioContentDto> createPortfolioContentDtos = getCreatePortfolioContentDtos();

        return CreatePortfolioDto.Request.createPortfolioDtoRequest(title, mainImageUrl, tags, createPortfolioContentDtos, drawingFieldIds, temporary);
    }

    private List<String> getTags() {
        return List.of("????????????", "????????? ??????");
    }


    private List<CreatePortfolioContentDto> getCreatePortfolioContentDtos() {
        return List.of(
                CreatePortfolioContentDto.createImageContent("imageUrl"),
                CreatePortfolioContentDto.createTextContent("imageUrl"),
                CreatePortfolioContentDto.createVideoContent("imageUrl")
        );
    }

    private Portfolio createPortfolio(String title,  PortfolioStatus status) {
        final ArtistUser artistUser = getArtistUser();
        final String tags = String.join(", ", getTags());
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields();
        final List<PortfolioContent> portfolioContents = getPortfolioContents();

        return Portfolio.createPortfolio(
                artistUser, "mainImageUrl", title,
                portfolioContents, tags, portfolioDrawingFields, status
        );
    }

    private List<PortfolioDrawingField> getPortfolioDrawingFields() {
        return List.of(
                createPortfolioDrawingField("??????????????????", "??????????????????", false),
                createPortfolioDrawingField("????????????", "????????????", false)
        );
    }

    private PortfolioDrawingField createPortfolioDrawingField(String fieldName, String description, boolean top) {
        return PortfolioDrawingField.createPortfolioDrawingField(
                DrawingField.createDrawingField(fieldName, description, DrawingFieldStatus.USED)
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
    @DisplayName("[??????] ?????? ??????????????? ??????")
    public void givenTempRequestDto_whenAddPortfolio_thenCreatePortfolioDtoResponse(){
        //given

        when(portfolioRepository.save(any(Portfolio.class)))
                .thenReturn(
                        getTempPortfolio()
                );
        //when
        final CreatePortfolioDto.Response response
                = portfolioService.addPortfolio(
                getArtistUser(), givenTempCreatePortfolioDtoRequest("??????")
        );

        //then
        assertAll(
                () -> assertThat(response)
                        .hasFieldOrPropertyWithValue("portfolioId", 1L)
                        .hasFieldOrPropertyWithValue("mainImageUrl", "defaultImageUrl")
                        .hasFieldOrPropertyWithValue("title", "??????")
                        .hasFieldOrPropertyWithValue("status", PortfolioStatus.TEMPORARY),
                () -> assertThat(response.getContents()).isNotEmpty(),
                () -> assertThat(response.getDrawingFields()).isEmpty(),
                () -> assertThat(response.getTags()).isEmpty()
        );

        verify(portfolioRepository).save(any(Portfolio.class));
    }

    private CreatePortfolioDto.Request givenTempCreatePortfolioDtoRequest(String title) {
        return createPortfolioDtoRequest(title, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, true);
    }

    private Portfolio getTempPortfolio() {
        final String tags = "";
        final List<PortfolioDrawingField> drawingFields = List.of();
        final String mainImageUrl = "defaultImageUrl";

        return getPortfolio(tags, PortfolioStatus.TEMPORARY, mainImageUrl, drawingFields);
    }

    private Portfolio getPortfolio(String tags, PortfolioStatus status, String mainImageUrl, List<PortfolioDrawingField> portfolioDrawingFields) {
        final List<PortfolioContent> portfolioContents = getPortfolioContents();
        return Portfolio.builder()
                .portfolioId((long) 1)
                .mainImageUrl(mainImageUrl)
                .title("??????")
                .view(0L)
                .status(status)
                .artistUser(getArtistUser())
                .portfolioContents(
                        portfolioContents
                )
                .tags(tags)
                .portfolioDrawingFields(
                        portfolioDrawingFields
                )
                .build();
    }

    @Test
    @DisplayName("[??????] ?????? ??????????????? ?????? ?????? - ??????????????? ?????? ??? ?????? ?????? 1 ????????? PortfolioDetailDto ??????")
    public void givenNormalPortfolioId_whenFindPortfolio_thenPortfolioIncreaseViewAndReturnPortfolioDetailDto(){
        //given

        final long normalPortfolioId = 1L;

        final Portfolio normalPortfolio = getPortfolioOnPortfolioStatus(PortfolioStatus.NORMAL);
        final Long beforeView = normalPortfolio.getView();

        when(portfolioRepository.findByPortfolioId(anyLong()))
                .thenReturn(
                        Optional.of(
                                normalPortfolio
                        )
                );
        //when 
        final PortfolioDetailDto portfolioDetailDto = portfolioService.findPortfolio(normalPortfolioId);

        //then
        assertThat(beforeView + 1)
                .isEqualTo(normalPortfolio.getView())
                .as("?????? ?????? ??? ????????? ?????? ??????");
        
        assertThat(portfolioDetailDto)
            .hasFieldOrPropertyWithValue("portfolioId", normalPortfolioId)
            .hasFieldOrPropertyWithValue("artistUser", "joon@naver.com")
            .hasFieldOrPropertyWithValue("title", "??????")
            .hasFieldOrPropertyWithValue("status", PortfolioStatus.NORMAL)
        ;

        verify(portfolioRepository).findByPortfolioId(eq(normalPortfolioId));

    }

    private Portfolio getPortfolioOnPortfolioStatus(PortfolioStatus status) {
        final String tags = "????????????,???????????????";
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields();
        return getPortfolio(tags, status, "mainImageUrl", portfolioDrawingFields);
    }

    @Test
    @DisplayName("[??????] ????????? ??????????????? ?????? ?????? - NOT_FOUND_PORTFOLIO ?????? ??????")
    public void givenWrongPortfolioId_whenFindPortfolio_thenNotFoundPortfolioError(){
        //given

        final long wrongPortfolioId = 999999L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;


        when(portfolioRepository.findByPortfolioId(anyLong()))
                .thenReturn(
                        Optional.empty()
                );
        //when 
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.findPortfolio(wrongPortfolioId)
        );

        //then
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRepository).findByPortfolioId(anyLong());
    }

    @Test
    @DisplayName("[??????] ????????? ??????????????? ?????? ?????? - NOT_FOUND_PORTFOLIO ?????? ??????")
    public void givenDeletedPortfolioId_whenFindPortfolio_thenNotFoundPortfolioError(){
        //given

        final long deletedPortfolioId = 2L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;


        when(portfolioRepository.findByPortfolioId(anyLong()))
                .thenReturn(
                        Optional.of(
                                getPortfolioOnPortfolioStatus(PortfolioStatus.DELETED)
                        )
                );
        //when 
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.findPortfolio(deletedPortfolioId)
        );

        //then
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRepository).findByPortfolioId(anyLong());
    }

    @Test
    @DisplayName("[??????] ????????? ??????????????? ?????? ?????? - NOT_FOUND_PORTFOLIO ?????? ??????")
    public void givenHiddenPortfolioId_whenFindPortfolio_thenNotFoundPortfolioError(){
        //given

        final long hiddenPortfolioId = 3L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;


        when(portfolioRepository.findByPortfolioId(anyLong()))
                .thenReturn(
                        Optional.of(
                                getPortfolioOnPortfolioStatus(PortfolioStatus.DELETED)
                        )
                );
        //when 
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.findPortfolio(hiddenPortfolioId)
        );

        //then
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRepository).findByPortfolioId(anyLong());
    }

    @Test
    @DisplayName("[??????] ?????? ?????? ??????????????? ?????? ?????? - ?????? ?????? ???????????? ?????? PortfolioDetailDto ??????")
    public void givenTempPortfolioId_whenFindPortfolio_thenNotIncreaseViewAndReturnPortfolioDetailDto(){
        //given
        final long givenTempPortfolioId = 1L;
        final Portfolio tempPortfolio = getTempPortfolio();
        final Long beforeView = tempPortfolio.getView();
        final ArtistUser artistUser = getArtistUser();

        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                tempPortfolio
                        )
                );
        //when
        final PortfolioDetailDto portfolioDetailDto = portfolioService.findTempPortfolio(givenTempPortfolioId, artistUser);

        //then
        assertThat(beforeView).isEqualTo(tempPortfolio.getView())
                .as("?????? ????????? ?????? ?????? ??? ????????? ??????");

        assertThat(portfolioDetailDto)
                .hasFieldOrPropertyWithValue("portfolioId", givenTempPortfolioId)
                .hasFieldOrPropertyWithValue("artistUser", "joon@naver.com")
                .hasFieldOrPropertyWithValue("title", "??????")
                .hasFieldOrPropertyWithValue("status", PortfolioStatus.TEMPORARY)
        ;
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(givenTempPortfolioId), any(ArtistUser.class));
    }

    @Test
    @DisplayName("[??????] ???????????? ?????? ??????????????? ?????? ?????? - NOT_FOUND_PORTFOLIO ?????? ??????")
    public void givenWrongPortfolioId_whenFindPortfolio_thenNotfoundPortfolioError(){
        //given
        final long wrongPortfolioId = 123456L;
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_FOUND_PORTFOLIO;

        when(portfolioRepository.findByPortfolioId(anyLong()))
                .thenReturn(
                        Optional.empty()
                );
        //when //then
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.findPortfolio(wrongPortfolioId)
        );
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findByPortfolioId(eq(wrongPortfolioId));
    }




    @Test
    @DisplayName("[??????] ??????????????? ????????????")
    public void givenPortfolioIdAndArtistUser_whenRemovePortfolio_thenNothing(){
        //given
        final long portfolioId = 1L;
        final ArtistUser artistUser = getArtistUser();
        final Portfolio portfolio = getPortfolioOnPortfolioStatus(PortfolioStatus.NORMAL);
        final PortfolioStatus beforeStatus = portfolio.getStatus();
        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                portfolio
                        )
                );
        //when
        portfolioService.removePortfolio(portfolioId, artistUser);

        //then
        assertAll(
                () -> assertThat(beforeStatus).isNotEqualTo(PortfolioStatus.DELETED),
                () -> assertThat(portfolio.getStatus()).isEqualTo(PortfolioStatus.DELETED)
        );

        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ??????????????? ??????????????? ?????? ?????? - ALREADY_DELETED_PORTFOLIO ?????? ??????")
    public void givenDeletedPortfolioId_whenRemovePortfolio_thenAlreadyDeletedPortfolioError(){
        //given
        final long portfolioId = 1L;
        final ArtistUser artistUser = getArtistUser();
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO;

        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                getPortfolioOnPortfolioStatus(PortfolioStatus.DELETED)
                        )
                );
        //when
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.removePortfolio(portfolioId, artistUser)
        );

        //then
         assertAll(
                 () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                 () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
         );
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
    }

    @Test
    @DisplayName("[??????] ????????? ??????????????? ??????????????? ?????? ?????? - CAN_NOT_REMOVE_PORTFOLIO ?????? ??????")
    public void givenHiddenPortfolioId_whenRemovePortfolio_thenCanNotRemovePortfolioError(){
        //given
        final long portfolioId = 1L;
        final ArtistUser artistUser = getArtistUser();
        final PortfolioErrorCode errorCode = PortfolioErrorCode.CAN_NOT_REMOVE_PORTFOLIO;

        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                getPortfolioOnPortfolioStatus(PortfolioStatus.HIDDEN)
                        )
                );
        //when
        final PortfolioException portfolioException = assertThrows(PortfolioException.class,
                () -> portfolioService.removePortfolio(portfolioId, artistUser)
        );

        //then
        assertAll(
                () -> assertThat(portfolioException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(portfolioException.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
    }

    @Test
    @DisplayName("[??????] ???????????? ??????????????? ?????? ??????")
    public void givenPortfolioId_whenRepresentPortfolio_then(){
        //given
        final long portfolioId = 1L;
        final ArtistUser artistUser = getArtistUser();
        final Portfolio portfolio = getPortfolioOnPortfolioStatus(PortfolioStatus.NORMAL);
        final List<Portfolio> portfoliosAnyOneRepresentative =
                List.of(
                    createPortfolio("??????1", PortfolioStatus.NORMAL),
                    createPortfolio("??????2", PortfolioStatus.NORMAL),
                    createPortfolio("?????? ???????????????", PortfolioStatus.REPRESENTATION),
                    createPortfolio("??????4", PortfolioStatus.NORMAL),
                    createPortfolio("??????5", PortfolioStatus.NORMAL)
        );

        final boolean beforeHasRepresentation = portfoliosAnyOneRepresentative.stream()
                .anyMatch(p-> p.isStatusEquals(PortfolioStatus.REPRESENTATION));

        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                portfolio
                        )
                );

        when(portfolioRepository.findAllByArtistUserAndStatus(any(ArtistUser.class), eq(PortfolioStatus.REPRESENTATION)))
                .thenReturn(
                        portfoliosAnyOneRepresentative
                );

        //when
        portfolioService.representPortfolio(portfolioId, artistUser);

        //then
        assertAll(
                () -> assertThat(beforeHasRepresentation).isTrue()
                    .as("?????? ??????????????? ?????? ??? ?????? ?????????????????? ?????? ?????????????????? ???????????? ??????."),
                () -> assertThat(portfolio.getStatus()).isEqualTo(PortfolioStatus.REPRESENTATION),
                () -> assertThat(beforeHasRepresentation)
                        .isTrue().as("????????? ?????????????????? ????????? ????????? ?????? ?????????????????? ?????? ?????? ??????"),
                () -> assertThat(
                        portfoliosAnyOneRepresentative.stream()
                        .anyMatch(p -> p.isStatusEquals(PortfolioStatus.REPRESENTATION))
                    ).isFalse().as("?????? ????????? ?????? ??? ????????? ??????????????? ?????????????????? ???????????????")
        );
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
        verify(portfolioRepository).findAllByArtistUserAndStatus(any(ArtistUser.class), eq(PortfolioStatus.REPRESENTATION));
    }

    @Test
    @DisplayName("[??????] ?????? ?????? ??????????????? ?????? ?????? - NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO ?????? ?????? ")
    public void givenTempPortfolioId_whenRepresentPortfolio_thenNotAllowRepresentativeTempPortfolioError(){
        //given
        final PortfolioErrorCode errorCode = PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO;

        final long portfolioId = 1L;
        final ArtistUser artistUser = getArtistUser();
        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
            .thenReturn(
                    Optional.of(
                            getPortfolioOnPortfolioStatus(PortfolioStatus.TEMPORARY)
                    )
            );
        //when
        final PortfolioException exception = assertThrows(PortfolioException.class,
            () -> portfolioService.representPortfolio(portfolioId, artistUser)
        )
        ;
        //then
        assertAll(
            () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
            () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
        verify(portfolioRepository, never()).findAllByArtistUserAndStatus(any(ArtistUser.class), any(PortfolioStatus.class));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ????????? ??????????????? ?????? ?????? - ALREADY_REGISTER_REPRESENTATIVE_PORTFOLIO ?????? ??????")
    public void givenRepresentativePortfolioId_whenRepresentPortfolio_thenAlreadyRegisterRepresentativePortfolioError(){
        //given
        final PortfolioErrorCode errorCode = PortfolioErrorCode.ALREADY_REGISTER_REPRESENTATIVE_PORTFOLIO;

        final ArtistUser artistUser = getArtistUser();
        final long portfolioId = 1L;

        when(portfolioRepository.findByPortfolioIdAndArtistUser(anyLong(), any(ArtistUser.class)))
                .thenThrow(
                        new PortfolioException(errorCode)
                );
        //when
        final PortfolioException exception = assertThrows(PortfolioException.class,
                () -> portfolioService.representPortfolio(portfolioId, artistUser)
        )
                ;
        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );
        verify(portfolioRepository).findByPortfolioIdAndArtistUser(eq(portfolioId), any(ArtistUser.class));
        verify(portfolioRepository, never()).findAllByArtistUserAndStatus(any(ArtistUser.class), any());
    }

}