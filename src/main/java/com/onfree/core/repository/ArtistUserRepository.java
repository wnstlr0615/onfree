package com.onfree.core.repository;

import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistUserRepository extends JpaRepository<ArtistUser, Long> {
    @Override
    @EntityGraph(attributePaths = "portfolioRoom")
    Optional<ArtistUser> findById(Long artistUserId);

    Integer countByEmail(String email);

}
