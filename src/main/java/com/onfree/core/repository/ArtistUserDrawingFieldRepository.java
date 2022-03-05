package com.onfree.core.repository;

import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.ArtistUserDrawingField;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistUserDrawingFieldRepository extends JpaRepository<ArtistUserDrawingField, Long> {
    void deleteAllByArtistUser(ArtistUser artistUser);

    @Query(value = "select ad.drawingField.drawingFieldId from ArtistUserDrawingField ad ")
    List<Long> findAllArtistUserDrawingFieldIdByArtistUser( ArtistUser artistUser);

    @Query(value = "select new com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto(df.drawingFieldId, df.fieldName, " +
            " case when audf.artistUserDrawingFieldId is null then false " +
            " else true " +
            " end )" +
            "from DrawingField df" +
            " left join ArtistUserDrawingField  as audf" +
            " on df = audf.drawingField and audf.artistUser = :artistUser")
    List<UsedDrawingFieldDto> findAllUsedDrawingFieldByArtistUser(@Param("artistUser") ArtistUser artistUser);


}
