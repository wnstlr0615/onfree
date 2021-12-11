package com.onfree.core.service;

import com.onfree.core.dto.user.CreateNormalUser;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.repository.UserRepository;
import com.onfree.error.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onfree.error.code.UserErrorCode.USER_EMAIL_DUPLICATED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateNormalUser.Response createNormalUser(CreateNormalUser.Request request) {
        duplicatedUserEmail(request.getEmail());
        return CreateNormalUser.Response
                .fromEntity(
                    saveUser(
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

    private NormalUser saveUser(NormalUser normalUser) {
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
}
