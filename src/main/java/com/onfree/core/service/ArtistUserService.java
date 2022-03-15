package com.onfree.core.service;

import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.BankInfo;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onfree.common.error.code.UserErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistUserService {
    private final PasswordEncoder passwordEncoder;
    private final ArtistUserRepository artistUserRepository;
    private final UserRepository userRepository;

    /** 회원가입 요청 */
    @Transactional
    public CreateArtistUserDto.Response addArtistUser(CreateArtistUserDto.Request request) {
        //이메일 중복 체크
        validateDuplicatedEmail(request.getEmail());
        
        return getCreateArtistUserDtoResponse( // Response Dto 로 변환
                saveArtistUser( // 작가유저 저장
                    bcryptPassword( // 패스워드 암호화
                            createArtistUser(request) // ArtistUser 생성
                    )
            )
        );
    }

    private void validateDuplicatedEmail(String email) {
        Integer cnt = getCountByEmail(email);
        if(cnt > 0){
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private Integer getCountByEmail(String email) {
        return artistUserRepository.countByEmail(email);
    }

    private ArtistUser createArtistUser(CreateArtistUserDto.Request request) {
        return request.toEntity();
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
        return artistUserRepository.save(ArtistUser);
    }

    private CreateArtistUserDto.Response getCreateArtistUserDtoResponse(ArtistUser entity) {
        return CreateArtistUserDto.Response.fromEntity(
                entity
        );
    }

    /** 사용자 정보 조회 */
    public ArtistUserDetailDto getUserDetail(Long userId) {
        return getArtistUserDetailDto( // ArtistUserDetailDto 로 변환
                getArtistUser(userId)
        );
    }

    private ArtistUser getArtistUser(Long userId) {
        return artistUserRepository.findByUserIdAndDeletedIsFalse(userId)
                .orElseThrow(
                        () -> new UserException(NOT_FOUND_USERID)
                );
    }

    private ArtistUserDetailDto getArtistUserDetailDto(ArtistUser artistUser) {
        return ArtistUserDetailDto.fromEntity(artistUser);
    }

    /** 사용자 deleted 처리 (수정 & 삭제 예정)  */
    @Transactional
    public void removeArtistUser(Long userId) {
        setArtistUserDeleted(
                getArtistUser(userId)
        );
    }

    private void setArtistUserDeleted(ArtistUser ArtistUser) {
        if(ArtistUserIsDeleted(ArtistUser)){
            throw new UserException(ALREADY_USER_DELETED);
        }
        ArtistUser.setDeleted();
    }

    private boolean ArtistUserIsDeleted(ArtistUser ArtistUser) {
        return ArtistUser.getDeleted();
    }

    /** 사용자 계정 수정 */

    @Transactional
    public void modifyArtistUser(Long userId, UpdateArtistUserDto.Request request) {
        //TODO 닉네임 중복 등 검증 추가
        ArtistUser artistUser = getArtistUser(userId);
        artistUserInfoUpdate(artistUser, request);

    }

    private ArtistUser artistUserInfoUpdate(ArtistUser artistUser, UpdateArtistUserDto.Request request) {
        BankInfo bankInfo = BankInfo.createBankInfo(request.getBankName(), request.getAccountNumber());
        artistUser.update(bankInfo, request.getAdultCertification(), request.getNickname(), request.getMobileCarrier(), request.getPhoneNumber(), request.getProfileImage(), request.getPortfolioUrl());
        return artistUser;
    }


    /*사용자 영업마크 수정*/
    @Transactional
    public void updateStatusMark(Long userId, StatusMarkDto statusMarkDto) {
        final ArtistUser artistUser = getArtistUser(userId);
        artistUser.updateStatusMark(statusMarkDto.getStatusMark());
    }

    /** 작가유저 닉네임 변경 */
    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        // 닉네임 중복 검사
        validateDuplicatedNickname(newNickname);

        //사용자 조회
        ArtistUser artistUser = getArtistUser(userId);

        //닉네임 업데이트
        artistUser.updateNickname(newNickname);
    }

    private void validateDuplicatedNickname(String nickname) {
        int countByNickname = userRepository.countByNickname(nickname);
        if(countByNickname > 0){
            throw new UserException(USER_NICKNAME_DUPLICATED);
        }
    }
}
