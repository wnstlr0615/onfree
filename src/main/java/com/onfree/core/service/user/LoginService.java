package com.onfree.core.service.user;

import com.onfree.common.constant.RedisConstant;
import com.onfree.common.error.code.LoginErrorCode;
import com.onfree.common.error.code.MailErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.LoginException;
import com.onfree.common.error.exception.MailSenderException;
import com.onfree.common.error.exception.UserException;
import com.onfree.controller.user.LoginController;
import com.onfree.core.dto.user.UpdatePasswordDto;
import com.onfree.core.entity.MailTemplate;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.MailTemplateRepository;
import com.onfree.core.repository.UserRepository;
import com.onfree.utils.MailComponent;
import com.onfree.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

import static com.onfree.common.constant.MailConstant.PASSWORD_RESET_TEMPLATE;
import static com.onfree.common.constant.RedisConstant.PASSWORD_RESET;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final MailTemplateRepository mailTemplateRepository;
    private final MailComponent mailComponent;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    /** 패스워드 인증용 메일 전송 */
    public void passwordReset(String email) {
        ExistUserByEmail(email);
        final String uuid = getRandomUuid();
        savePasswordResetRedis(email, uuid);
        sendPasswordResetMail(email, uuid);
    }

    private void ExistUserByEmail(String email) {
        if(userRepository.countByEmail(email) != 1){
            log.error("not found email - email : {}", email);
            throw new UserException(UserErrorCode.NOT_FOUND_USER_EMAIL);
        }
    }
    private void savePasswordResetRedis(String email, String uuid) {
        final String key = PASSWORD_RESET + uuid;
        final Duration timeout = Duration.ofSeconds(300);
        redisUtil.addData(key, email, timeout);
        log.info("save PasswordReset  - uuid :  {}, email : {}", uuid, email);
    }


    private void sendPasswordResetMail(String email, String uuid) {
        MailTemplate mailTemplate = getMailTemplate(PASSWORD_RESET_TEMPLATE);
        final String content = getContent(mailTemplate, uuid);
        mailComponent.sendMail(email, mailTemplate.getTitle(), content);
        log.info(" send mail success - email : {} , template : {}",email, PASSWORD_RESET_TEMPLATE);
    }

    private String getContent(MailTemplate mailTemplate, String uuid) {
        //TODO 링크 MailTemplate에 같이 적용하기
        URI uri = linkTo(LoginController.class).slash("password").slash("uuid").slash(uuid).toUri();
        return mailTemplate.getContent().replace("<URL>", uri.toString());
    }



    private MailTemplate getMailTemplate(String templateName) {
        return mailTemplateRepository.findByMailTemplateName(templateName)
                .orElseThrow(() -> new MailSenderException(MailErrorCode.WRONG_MAIL_ATTRIBUTE));
    }
    private String getRandomUuid() {
        return UUID.randomUUID().toString();
    }

    /** 패스워드 메일 인증 후 패스워드 업데이트*/
    @Transactional
    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        userPasswordReset( // 패스워드 재설정
            getUserByEmail( // 사용자 조회
                    getEmailByPasswordResetUUID(updatePasswordDto.getUuid()) //  해당 uuid를 가지는 회원 id를 레디스에서 조회
            ),
                updatePasswordDto.getNewPassword()
        );
    }

    private String getEmailByPasswordResetUUID(String uuid) {
        final String key = PASSWORD_RESET + uuid;
        final String email = redisUtil.getData(key);

        if(!StringUtils.hasText(email)){
            log.error("Passsword Reset UUID Expired - key : {}", key);
            throw new LoginException(LoginErrorCode.EXPIRED_PASSWORD_RESET_UUID);
        }
        return email;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USER_EMAIL));
    }

    private void userPasswordReset(User user, String newPassword) {
        final String bcryptPassword = passwordEncoder.encode(newPassword);
        user.resetPassword(bcryptPassword);
    }


    /** 리플래쉬 토큰 저장*/
    public void saveRefreshToken(String username, String refreshToken) {
        final String key = RedisConstant.USER_REFRESH_TOKEN + username;
        final Duration timeout = Duration.ofDays(7);
        redisUtil.addData(key, refreshToken, timeout);
    }

    public boolean isWrongRefreshToken(String username, String oldRefreshToken) {
        final String refreshToken = redisUtil.getData(RedisConstant.USER_REFRESH_TOKEN + username);
        return !StringUtils.hasText(refreshToken) || isNotMatchRefreshToken(refreshToken, oldRefreshToken);
    }

    public void deleteRefreshTokenByUsername(String username) {
        final String key = RedisConstant.USER_REFRESH_TOKEN + username;
        redisUtil.deleteData(key);
    }

    public boolean isNotMatchRefreshToken(String curRefreshToken, String oldRefreshToken) {
        return !curRefreshToken.equals(oldRefreshToken);
    }
}
