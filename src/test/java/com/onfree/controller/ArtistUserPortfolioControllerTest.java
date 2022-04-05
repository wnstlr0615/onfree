package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.config.webmvc.resolver.CurrentArtistUserArgumentResolver;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.ArtistUserPortfolioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistUserPortfolioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArtistUserPortfolioControllerTest extends ControllerBaseTest {
    @MockBean
    private ArtistUserPortfolioService artistUserPortfolioService;

    @MockBean(CurrentArtistUserArgumentResolver.class)
    CurrentArtistUserArgumentResolver currentArtistUserArgumentResolver;

    @Test
    @DisplayName("[성공][GET] 작가 사용자 포트폴리오 조회")
    public void givenUserIdAndPage_whenPortfolioList_thenReturnPagingPortfolioSimpleDtos() throws Exception{
        //given
        final long userId = 1L;

        final List<PortfolioSimpleDto> portfolioSimpleDtos = getIteraterPortfolioSimpleDtos(6, PortfolioStatus.NORMAL);
        final PageRequest pageRequest = PageRequest.of(0, 6);
        when(artistUserPortfolioService.findAllPortfolioByUserId(anyLong(), any(PageRequest.class)))
                .thenReturn(
                        new PageImpl<>(portfolioSimpleDtos, pageRequest, portfolioSimpleDtos.size())
                );
        //when //then
        mvc.perform(get("/api/v1/users/artist/{userId}/portfolios", userId)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.items[0].portfolioId").value(1L))
                .andExpect(jsonPath("$._embedded.items[0].mainImageUrl").value("mainImageUrl"))
                .andExpect(jsonPath("$._embedded.items[0].title").value("포트폴리오 제목 1"))
                .andExpect(jsonPath("$._embedded.items[0].view").value(0L))
                .andExpect(jsonPath("$._embedded.items[0].status").value(PortfolioStatus.NORMAL.toString()))
                .andExpect(jsonPath("$._embedded.items[0]._links.detail-portfolio.href").exists())
        ;
        verify(artistUserPortfolioService).findAllPortfolioByUserId(eq(userId), any(PageRequest.class));
    }

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET] 작가 사용자 임시 저장 포트폴리오 조회")
    public void givenUserIdAndPage_whenTempPortfolioList_thenReturnPagingPortfolioSimpleDtos() throws Exception{
        //given
        final long userId = 1L;

        final List<PortfolioSimpleDto> portfolioSimpleDtos = getIteraterPortfolioSimpleDtos(6, PortfolioStatus.TEMPORARY);
        final PageRequest pageRequest = PageRequest.of(0, 6);
        when(artistUserPortfolioService.findAllTempPortfolioByArtistUser(any(ArtistUser.class), anyInt()))
                .thenReturn(
                        new PageImpl<>(portfolioSimpleDtos, pageRequest, portfolioSimpleDtos.size())
                );

        when(currentArtistUserArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                        getArtistUser()
                );
        //when //then
        mvc.perform(get("/api/v1/users/artist/me/portfolios/temp")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.items[0].portfolioId").value(1L))
                .andExpect(jsonPath("$._embedded.items[0].mainImageUrl").value("mainImageUrl"))
                .andExpect(jsonPath("$._embedded.items[0].title").value("포트폴리오 제목 1"))
                .andExpect(jsonPath("$._embedded.items[0].view").value(0L))
                .andExpect(jsonPath("$._embedded.items[0].status").value(PortfolioStatus.TEMPORARY.toString()))
                .andExpect(jsonPath("$._embedded.items[0]._links.detail-portfolio.href").exists())
        ;
        verify(artistUserPortfolioService).findAllTempPortfolioByArtistUser(any(ArtistUser.class), anyInt());
    }

    private List<PortfolioSimpleDto> getIteraterPortfolioSimpleDtos(int size, PortfolioStatus status) {
        return IntStream.rangeClosed(1, size)
                .mapToObj(operand ->
                        PortfolioSimpleDto
                                .createPortfolioSimpleDto(operand, "포트폴리오 제목 "+ operand, "mainImageUrl", status)
                )
                .collect(toList());
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

}