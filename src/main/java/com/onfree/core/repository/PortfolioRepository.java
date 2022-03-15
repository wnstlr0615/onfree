package com.onfree.core.repository;

import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /** 포트폴리오 전체 조회 */
    List<Portfolio> findByArtistUser(ArtistUser artistUser);

    Page<Portfolio> findByArtistUserAndStatusIn(ArtistUser artistUser, List<PortfolioStatus> statuses, Pageable pageable);

    /**포트폴리오 상세 조회  */
    @EntityGraph(value = "portfolio-details-graph",type = EntityGraph.EntityGraphType.LOAD)
    Optional<Portfolio> findByPortfolioIdAndArtistUser(Long portfolioId, ArtistUser artistUser);

    /** 포트폴리오 상세 조회*/
    @EntityGraph(value = "portfolio-details-graph",type = EntityGraph.EntityGraphType.LOAD)
    Optional<Portfolio> findByPortfolioId(Long portfolioId);

    /** 작가 유저와 상태에 따른 포트폴리오 전체 조회 */
    Page<Portfolio> findPageByArtistUserAndStatus(ArtistUser artistUser, PortfolioStatus temporary, Pageable pageable);

    List<Portfolio> findAllByArtistUserAndStatus(ArtistUser artistUser, PortfolioStatus temporary);

}
