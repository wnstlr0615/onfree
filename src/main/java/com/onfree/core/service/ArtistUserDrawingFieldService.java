package com.onfree.core.service;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.ArtistUserDrawingField;
import com.onfree.core.entity.DrawingField;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.ArtistUserDrawingFieldRepository;
import com.onfree.core.repository.DrawingFieldRepository;
import com.onfree.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistUserDrawingFieldService {
    private final ArtistUserDrawingFieldRepository artistUserDrawingFieldRepository;
    private final UserRepository userRepository;
    private final DrawingFieldRepository drawingFieldRepository;

    /** 그림분야 수정*/
    @Transactional
    public void updateDrawingFields(Long userId, UpdateDrawingFieldsDto updateDrawingFieldsDto) {
        final ArtistUser artistUser = getUser(userId);
        final List<DrawingField> updateDrawingFieldList = getUpdateDrawingFieldList(updateDrawingFieldsDto);
        deletedAllArtistUserDrawingFieldByArtistUser(artistUser);
        saveAllArtistUserDrawingFieldList(artistUser, updateDrawingFieldList);
    }

    private ArtistUser getUser(Long userId) {
        return (ArtistUser) userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));
    }

    private List<DrawingField> getUpdateDrawingFieldList(UpdateDrawingFieldsDto updateDrawingFieldsDto) {
        final List<DrawingField> updateDrawingFieldList = drawingFieldRepository.findAllByDisabledIsFalseAndDrawingFieldIdIn(
                updateDrawingFieldsDto.getDrawingFields()
        );
        if(isNotValidUpdateDrawingFieldIdList(updateDrawingFieldsDto, updateDrawingFieldList)){
            throw new DrawingFieldException(DrawingFieldErrorCode.WRONG_DRAWING_FIELD);
        }
        return updateDrawingFieldList;
    }

    private boolean isNotValidUpdateDrawingFieldIdList(UpdateDrawingFieldsDto updateDrawingFieldsDto, List<DrawingField> updateDrawingFieldList) {
        return updateDrawingFieldsDto.getDrawingFields().size() != updateDrawingFieldList.size();
    }

    private void deletedAllArtistUserDrawingFieldByArtistUser(ArtistUser artistUser) {
        artistUserDrawingFieldRepository.deleteAllByArtistUser(artistUser);
    }

    private void saveAllArtistUserDrawingFieldList(ArtistUser artistUser, List<DrawingField> updateDrawingFieldList) {
        artistUserDrawingFieldRepository.saveAll(
                createArtistUserDrawingFieldList(artistUser, updateDrawingFieldList)
        );
    }

    private List<ArtistUserDrawingField> createArtistUserDrawingFieldList(ArtistUser artistUser, List<DrawingField> updateDrawingFields) {
        return updateDrawingFields.stream().map(
                drawingField -> new ArtistUserDrawingField(null, artistUser, drawingField)
        ).collect(Collectors.toList());
    }

    /** 사용중인 그림분야 가져오기 */
    public List<UsedDrawingFieldDto> getAllArtistUserUsedDrawingFields(Long userId) {
        final ArtistUser artistUser = getUser(userId);
        return getUsedDrawingFieldDtoList(
                getAllDrawingFieldList(),
                getAllArtistUserHasDrawingFieldIdListByArtistUser(artistUser)
        );
    }

    private List<DrawingField> getAllDrawingFieldList() {
        return drawingFieldRepository.findAllByDisabledIsFalseOrderByTopDesc();
    }

    private List<Long> getAllArtistUserHasDrawingFieldIdListByArtistUser(ArtistUser artistUser) {
        return artistUserDrawingFieldRepository.findAllArtistuserDrawingFieldIdByArtistUser(artistUser);
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDtoList(List<DrawingField> allDrawingFields, List<Long> artistUserHasDrawingFieldIdList) {
        if(artistUserHasDrawingFieldIdList.isEmpty()){
            return allDrawingFields.stream()
                    .map(drawingField -> createUsedDrawingFieldDto(drawingField,false))
                    .collect(Collectors.toList());
        }

        return allDrawingFields.stream().map(
                drawingField -> {
                    if(artistUserHasDrawingFieldIdList.contains(drawingField.getDrawingFieldId())){
                        return createUsedDrawingFieldDto(drawingField,true);
                    }
                    return createUsedDrawingFieldDto(drawingField,false);
                }
        ).collect(Collectors.toList());
    }

    private UsedDrawingFieldDto createUsedDrawingFieldDto(DrawingField drawingField, boolean used) {
        return UsedDrawingFieldDto.builder()
                .drawingFieldId(drawingField.getDrawingFieldId())
                .drawingFieldName(drawingField.getFieldName())
                .used(used)
                .build();
    }
}