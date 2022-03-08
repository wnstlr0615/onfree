package com.onfree.core.repository;

import com.onfree.core.entity.portfolioroom.PortfolioRoom;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRoomRepository extends JpaRepository<PortfolioRoom, Long> {
    @EntityGraph(attributePaths = "artistUser")
    Optional<PortfolioRoom> findByPortfolioRoomURL(String portfolioRoomURL);

    Optional<PortfolioRoom> findByArtistUser(ArtistUser artistUser);

}
