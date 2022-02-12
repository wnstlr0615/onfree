package com.onfree.core.repository;

import com.onfree.core.entity.Portfolio;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    /** 삭제되지 않은 포트폴리오 중 사용자와 임시 저장 유무에 따라 전체 조회 **/
    List<Portfolio> findByArtistUserAndTemporaryAndDeletedIsFalse(ArtistUser artistUser, boolean temporary);

    /** 삭제되지 않은 포트폴리오 중 사용자에 따라 전체 조회 **/
    List<Portfolio> findByArtistUserAndDeletedIsFalse(ArtistUser artistUser);

    /** 삭제되지 않은 포트폴리오중 아이디와 임시저장에 유무에 따라 단건 조회 **/
    @EntityGraph(value = "portfolio-details-graph",type = EntityGraph.EntityGraphType.LOAD)
    Optional<Portfolio> findByPortfolioIdAndTemporaryAndDeletedIsFalse(Long portfolioId, boolean temporary);

}
