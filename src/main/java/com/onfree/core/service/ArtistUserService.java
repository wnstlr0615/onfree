package com.onfree.core.service;

import com.onfree.controller.StatusMarkDto;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetail;
import com.onfree.core.dto.user.artist.CreateArtistUser;
import com.onfree.core.dto.user.artist.UpdateArtistUser;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.UserRepository;
import com.onfree.common.error.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onfree.common.error.code.UserErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 요청 */
    @Transactional
    public CreateArtistUser.Response createArtistUser(CreateArtistUser.Request request) {
        //TODO Redis 를 통한 인증 검사 후 회원가입
        duplicatedUserEmail(request.getEmail());
        return CreateArtistUser.Response
                .fromEntity(
                    saveArtistUser(
                            bcryptPassword(request.toEntity())
                    )
        );
    }

    private ArtistUser bcryptPassword(ArtistUser artistUser) {
        artistUser.encryptPassword(
                passwordEncoder.encode(
                        artistUser.getPassword()
                )
        );
        return artistUser;
    }

    private ArtistUser saveArtistUser(ArtistUser ArtistUser) {
        return userRepository.save(ArtistUser);
    }

    private void duplicatedUserEmail(String email) {
        Integer cnt = getCountByEmail(email);
        if(cnt > 0){
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private Integer getCountByEmail(String email) {
        return userRepository.countByEmail(
                email
        );
    }
    /** 사용자 정보 조회*/
    public ArtistUserDetail getUserDetail(Long userId) {
        return ArtistUserDetail.fromEntity(
                getArtistUser(userId)
        );
    }

    private ArtistUser getArtistUser(Long userId) {
        return (ArtistUser) userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserException(NOT_FOUND_USERID)
                );
    }
    /** 사용자 deleted 처리*/
    @Transactional
    public DeletedUserResponse deletedArtistUser(Long userId) {
        return DeletedUserResponse.fromEntity(
                setArtistUserDeleted(
                        getArtistUser(userId)
                )
        );
    }

    private ArtistUser setArtistUserDeleted(ArtistUser ArtistUser) {
        if(ArtistUserIsDeleted(ArtistUser)){
            throw new UserException(ALREADY_USER_DELETED);
        }
        ArtistUser.setDeleted();
        return ArtistUser;
    }

    private boolean ArtistUserIsDeleted(ArtistUser ArtistUser) {
        return ArtistUser.getDeleted();
    }

    /** 사용자 계정 수정*/
    @Transactional
    public UpdateArtistUser.Response modifiedUser(Long userId, UpdateArtistUser.Request request) {
        ArtistUser ArtistUser = getArtistUser(userId);
        ArtistUser.update(request);
        return UpdateArtistUser.Response
                .fromEntity(ArtistUser);
    }
    /*사용자 영업마크 수정*/
    @Transactional
    public void updateStatusMark(Long userId, StatusMarkDto statusMarkDto) {
        final ArtistUser artistUser = getArtistUser(userId);
        artistUser.updateStatusMark(statusMarkDto.getStatusMark());
    }
}
