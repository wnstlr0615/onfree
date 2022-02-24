package com.onfree.core.service;

import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.BankInfo;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onfree.common.error.code.UserErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NormalUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 요청
     */
    @Transactional
    public CreateNormalUserDto.Response addNormalUser(CreateNormalUserDto.Request request) {
        duplicatedUserEmail(request.getEmail()); //이메일 중복 체크

        return getCreateNormalUserDtoResponse(
                saveNormalUser( // NormalUser 저장
                        bcryptPassword( //패스워드 암호화
                                createNormalUserFromDto(request) // NormalUser 생성
                        )
                )
        );
    }

    private void duplicatedUserEmail(String email) {
        if (getCountByEmail(email) > 0) {
            throw new UserException(USER_EMAIL_DUPLICATED);
        }
    }

    private Integer getCountByEmail(String email) {
        return userRepository.countByEmail(
                email
        );
    }

    private NormalUser createNormalUserFromDto(CreateNormalUserDto.Request request) {
        return request.toEntity();
    }

    private NormalUser bcryptPassword(NormalUser normalUser) {
        normalUser.encryptPassword(
                passwordEncoder.encode(
                        normalUser.getPassword()
                )
        );
        return normalUser;
    }

    private NormalUser saveNormalUser(NormalUser normalUser) {
        return userRepository.save(normalUser);
    }

    private CreateNormalUserDto.Response getCreateNormalUserDtoResponse(NormalUser entity) {
        return CreateNormalUserDto.Response.fromEntity(entity);
    }

    /**
     * 사용자 정보 조회
     */
    public NormalUserDetailDto getUserDetail(Long userId) {
        return getNormalUserDetailDto( //NormalUserDetailDto 로 변환
                getNormalUser(userId)
        );
    }

    private NormalUserDetailDto getNormalUserDetailDto(NormalUser normalUser) {
        return NormalUserDetailDto.fromEntity(
                normalUser
        );
    }

    private NormalUser getNormalUser(Long userId) {
        return (NormalUser) userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserException(NOT_FOUND_USERID)
                );
    }

    /**
     * 사용자 deleted 처리
     */
    @Transactional
    public void removeNormalUser(Long userId) {
        setNormalUserDeleted( // 삭제처리
                getNormalUser(userId)//사용자 조회
        );
    }

    private void setNormalUserDeleted(NormalUser normalUser) {
        if (normalUserIsDeleted(normalUser)) {
            throw new UserException(ALREADY_USER_DELETED);
        }
        normalUser.setDeleted();
    }

    private boolean normalUserIsDeleted(NormalUser normalUser) {
        return normalUser.getDeleted();
    }

    /**
     * 사용자 계정 수정
     */
    @Transactional
    public void modifyNormalUser(Long userId, UpdateNormalUserDto.Request request) {
        NormalUser normalUser = getNormalUser(userId);
        normalUserInfoUpdate(normalUser, request);
    }

    private void normalUserInfoUpdate(NormalUser normalUser, UpdateNormalUserDto.Request request) {
        BankInfo bankInfo = BankInfo.createBankInfo(request.getBankName(), request.getAccountNumber());
        normalUser.update(bankInfo, request.getAdultCertification(), request.getNickname(), request.getNewsAgency(),request.getPhoneNumber(), request.getProfileImage());
    }
}
