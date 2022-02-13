package com.onfree.core.service;

import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.NormalUser;
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
public class NormalUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원가입 요청 */
    @Transactional
    public CreateNormalUserDto.Response createdNormalUser(CreateNormalUserDto.Request request) {
        //TODO Redis 를 통한 인증 검사 후 회원가입
        duplicatedUserEmail(request.getEmail());
        return CreateNormalUserDto.Response
                .fromEntity(
                    saveNormalUser(
                            bcryptPassword(request.toEntity())
                    )
        );
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
    public NormalUserDetailDto getUserDetail(Long userId) {
        return NormalUserDetailDto.fromEntity(
                getNormalUser(userId)
        );
    }

    private NormalUser getNormalUser(Long userId) {
        return (NormalUser) userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserException(NOT_FOUND_USERID)
                );
    }
    /** 사용자 deleted 처리*/
    @Transactional
    public DeletedUserResponse deletedNormalUser(Long userId) {
        return DeletedUserResponse.fromEntity(
                setNormalUserDeleted(
                        getNormalUser(userId)
                )
        );
    }

    private NormalUser setNormalUserDeleted(NormalUser normalUser) {
        if(normalUserIsDeleted(normalUser)){
            throw new UserException(ALREADY_USER_DELETED);
        }
        normalUser.setDeleted();
        return normalUser;
    }

    private boolean normalUserIsDeleted(NormalUser normalUser) {
        return normalUser.getDeleted();
    }

    /** 사용자 계정 수정*/
    @Transactional
    public UpdateNormalUserDto.Response modifyedUser(Long userId, UpdateNormalUserDto.Request request) {
        NormalUser normalUser = getNormalUser(userId);
        normalUser.update(request);
        return UpdateNormalUserDto.Response
                .fromEntity(normalUser);
    }
}
