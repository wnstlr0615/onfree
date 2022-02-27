package com.onfree.core.service;

import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
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
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.core.repository.PortfolioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistUserPortfolioServiceTest {
    @Mock
    PortfolioRepository portfolioRepository;
    @Mock
    ArtistUserRepository artistUserRepository;
    @InjectMocks
    ArtistUserPortfolioService artistUserPortfolioService;
    

    @Test
    @DisplayName("[성공] 작가 포트폴리오 조회")
    public void givenUserId_whenFindAllPortfolioByUserId_thenPortfolioSimpleDtos(){
        //given
        final long userId = 1L;
        final boolean temporary = false;
        final ArtistUser artistUser = getArtistUser();
        final PageRequest pageRequest = PageRequest.of(0, 3);
        final List<Portfolio> portfolio = List.of(
                createPortfolio("제목1", PortfolioStatus.NORMAL),
                createPortfolio("제목2", PortfolioStatus.NORMAL),
                createPortfolio("제목3", PortfolioStatus.NORMAL)
        );



        when(artistUserRepository.findById(userId))
                .thenReturn(
                        Optional.ofNullable(
                                artistUser
                        )
                );


        when(portfolioRepository.findByArtistUserAndStatusIn(any(ArtistUser.class), any(), any(Pageable.class)))
                .thenReturn( new PageImpl(portfolio, pageRequest, 3));
        //when
        final Page<PortfolioSimpleDto> portfolioSimpleDtos
                = artistUserPortfolioService.findAllPortfolioByUserId(userId, pageRequest);

        //then
        assertThat(portfolioSimpleDtos.getTotalElements()).isEqualTo(3);
        assertThat(portfolioSimpleDtos.getContent().get(0))
                .hasFieldOrPropertyWithValue("mainImageUrl", "mainImageUrl")
                .hasFieldOrPropertyWithValue("title", "제목1")
                .hasFieldOrPropertyWithValue("view", 0L)
        ;
        verify(artistUserRepository).findById(eq(userId));
        verify(portfolioRepository).findByArtistUserAndStatusIn(any(ArtistUser.class), any(), any(Pageable.class));
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
    private List<String> getTags() {
        return List.of("일러스트", "캐릭터 작업");
    }

    private List<PortfolioContent> getPortfolioContents() {
        return List.of(
                VideoContent.createVideoContent("videoUrl"),
                ImageContent.createImageContent("imageUrl"),
                TextContent.createTextContent("text")
        );
    }

    private List<PortfolioDrawingField> getPortfolioDrawingFields() {
        return List.of(
                createPortfolioDrawingField("캐릭터디자인", "캐릭터디자인",  DrawingFieldStatus.USED),
                createPortfolioDrawingField("일러스트", "일러스트",  DrawingFieldStatus.USED)
        );
    }

    private PortfolioDrawingField createPortfolioDrawingField(String fieldName, String description,  DrawingFieldStatus status) {
        return PortfolioDrawingField.createPortfolioDrawingField(
                DrawingField.createDrawingField(fieldName, description, status)
        );
    }

    @Test
    @DisplayName("[성공] 작가 임시 저장 포트폴리오 전체 조회")
    public void giveArtistUser_whenfindTempPortfolioByArtistUser_thenPortfolioSimpleDtos(){
        //given
        final ArtistUser artistUser = getArtistUser();
        final PageRequest pageRequest = PageRequest.of(0, 6);
        final List<Portfolio> portfolio = List.of(
                createPortfolio("제목1", PortfolioStatus.TEMPORARY),
                createPortfolio("제목2", PortfolioStatus.TEMPORARY),
                createPortfolio("제목3", PortfolioStatus.TEMPORARY)
        );
        when(portfolioRepository.findPageByArtistUserAndStatus(any(ArtistUser.class), eq(PortfolioStatus.TEMPORARY), any(Pageable.class)))
                .thenReturn(
                        new PageImpl(portfolio, pageRequest, 3)
                );
        //when
        final Page<PortfolioSimpleDto> portfolioSimpleDtos
                = artistUserPortfolioService.findAllTempPortfolioByArtistUser(artistUser, 0);

        //then
        assertThat(portfolioSimpleDtos.getTotalElements()).isEqualTo(3);
        assertThat(portfolioSimpleDtos.getContent().get(0))
                .hasFieldOrPropertyWithValue("mainImageUrl", "mainImageUrl")
                .hasFieldOrPropertyWithValue("title", "제목1")
                .hasFieldOrPropertyWithValue("view", 0L)
        ;
        verify(portfolioRepository).findPageByArtistUserAndStatus(any(ArtistUser.class), eq(PortfolioStatus.TEMPORARY), eq(pageRequest));
    }
}