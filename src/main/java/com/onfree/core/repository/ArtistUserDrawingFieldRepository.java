package com.onfree.core.repository;

import com.onfree.core.entity.ArtistUserDrawingField;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistUserDrawingFieldRepository extends JpaRepository<ArtistUserDrawingField, Long> {
    void deleteAllByArtistUser(ArtistUser artistUser);

    @Query(value = "select ad.drawingField.drawingFieldId from ArtistUserDrawingField ad where ad.artistUser=:artistUser")
    List<Long> findAllArtistuserDrawingFieldIdByArtistUser(@Param("artistUser") ArtistUser artistUser);
}
