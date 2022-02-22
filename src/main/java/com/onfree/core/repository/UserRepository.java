package com.onfree.core.repository;

import com.onfree.core.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedIsFalse(String email);
    Integer countByEmail(String email);

    int countByNickname(String nickName);


    @Query(value = "select count(a) from ArtistUser a where a.portfolioRoom.portfolioRoomURL = :personalUrl")
    int countByPortfolioUrlOnlyArtist(@Param("personalUrl") String personalUrl);
}
