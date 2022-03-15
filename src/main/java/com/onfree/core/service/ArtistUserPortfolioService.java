package com.onfree.core.service;

import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.core.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistUserPortfolioService {
    private final int PAGE_SIZE = 6;
    private final PortfolioRepository portfolioRepository;
    private final ArtistUserRepository artistUserRepository;

    /** 작가 포트폴리오 전체 조회 */
    public Page<PortfolioSimpleDto> findAllPortfolioByUserId(Long userId, PageRequest pageable) {
        return getPagingPortfolioSimpleDtos(//Portfolio 리스트를  PortfolioSimpleDto 리스트로 변환
                getPagingPortfoliosByArtistUser( // 작가유저 포트폴리오 조회
                        getArtistUserEntity(userId), // 작가 유저 조회,
                        pageable
                )
        );
    }


    private ArtistUser getArtistUserEntity(Long userId) {
        return artistUserRepository.findById(userId)
                .orElseThrow(
                        () ->  new UserException(UserErrorCode.NOT_FOUND_USERID)
                );
    }

    private Page<Portfolio> getPagingPortfoliosByArtistUser(ArtistUser artistUser, Pageable pageable) {
        final List<PortfolioStatus> readableStatus = List.of(PortfolioStatus.NORMAL, PortfolioStatus.REPRESENTATION);
        return portfolioRepository.findByArtistUserAndStatusIn(artistUser, readableStatus, pageable);
    }

    private Page<PortfolioSimpleDto> getPagingPortfolioSimpleDtos(Page<Portfolio> portfolios) {
        return  portfolios.map(PortfolioSimpleDto::fromEntity);
    }

    /** 작가 유저 임시 포트폴리오 전체 조회 */
    public Page<PortfolioSimpleDto> findAllTempPortfolioByArtistUser(ArtistUser artistUser, int page) {
        final PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);

        return getPagingPortfolioSimpleDtos( //Portfolio 리스트를  PortfolioSimpleDto 리스트로 변환
                getPagingTempPortfoliosByArtistUser( // 작가유저 임시 저장 포트폴리오 조회
                        artistUser, pageRequest
                )
        );
    }

    private Page<Portfolio> getPagingTempPortfoliosByArtistUser(ArtistUser artistUser, Pageable pageable) {
        return portfolioRepository.findPageByArtistUserAndStatus(artistUser, PortfolioStatus.TEMPORARY, pageable);
    }




}
