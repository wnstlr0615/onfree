package com.onfree.core.service;

import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
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
    public CreateArtistUserDto.Response createArtistUser(CreateArtistUserDto.Request request) {
        //이메일 중복 체크
        duplicatedUserEmail(request.getEmail());
        return getCreateArtistUserDtoResponse( // Response Dto 로 변환
                saveArtistUser( // 작가유저 저장
                    bcryptPassword( // 패스워드 암호화
                            request.toEntity()
                    )
            )
        );
    }

    private CreateArtistUserDto.Response getCreateArtistUserDtoResponse(ArtistUser entity) {
        return CreateArtistUserDto.Response.fromEntity(
                entity
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
    public ArtistUserDetailDto getUserDetail(Long userId) {
        return getArtistUserDetailDto( // ArtistUserDetailDto 로 변환
                getArtistUser(userId) // 사용자 정보 조회
        );
    }

    private ArtistUserDetailDto getArtistUserDetailDto(ArtistUser artistUser) {
        return ArtistUserDetailDto.fromEntity(
                artistUser // 사용자 조회
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
    public UpdateArtistUserDto.Response modifiedUser(Long userId, UpdateArtistUserDto.Request request) {
        ArtistUser ArtistUser = getArtistUser(userId);
        ArtistUser.update(request);
        return UpdateArtistUserDto.Response
                .fromEntity(ArtistUser);
    }
    /*사용자 영업마크 수정*/
    @Transactional
    public void updateStatusMark(Long userId, StatusMarkDto statusMarkDto) {
        final ArtistUser artistUser = getArtistUser(userId);
        artistUser.updateStatusMark(statusMarkDto.getStatusMark());
    }
}
