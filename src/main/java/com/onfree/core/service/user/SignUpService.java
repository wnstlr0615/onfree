package com.onfree.core.service.user;

import com.onfree.common.error.code.MailErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.MailSenderException;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.controller.user.SignupController;
import com.onfree.core.entity.MailTemplate;
import com.onfree.core.repository.MailTemplateRepository;
import com.onfree.core.repository.UserRepository;
import com.onfree.utils.MailComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static com.onfree.common.constant.RedisConstant.SIGNUP_UUID;
import static com.onfree.common.constant.RedisConstant.SIGNUP_VERIFICATION;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class SignUpService {

    private final MailTemplateRepository mailTemplateRepository;
    private final UserRepository userRepository;
    private final MailComponent mailComponent;
    private final StringRedisTemplate redisTemplate;

    @Value("${server.host.api}")
    private String host;

    /** 이메일 인증 */
    @Async(value = "getAsyncExecutor")
    public void asyncEmailVerify(String email) {
        validDuplicatedEmail(email);
        String uuid = getRandomUuid();
        saveSignUpUuidRedis(email, uuid);
        MailTemplate checkEmailTemplate = getMailTemplate("CHECK_EMAIL");
        String content = getContent(checkEmailTemplate.getContent(), uuid);
        mailComponent.sendMail(email, checkEmailTemplate.getTitle(), content);
    }

    private void saveSignUpUuidRedis(String email, String uuid) {
        redisTemplate.opsForValue().set(SIGNUP_UUID + uuid, email, Duration.ofSeconds(300));
    }

    private String getContent( String content, String uuid) {
        //TODO 링크 MailTemplate에 같이 적용하기
        String uri =  host + linkTo(SignupController.class).slash("uuid").slash(uuid);
        return content.replace("<URL>", uri);
    }

    private void validDuplicatedEmail(String email) {
        if(userRepository.countByEmail(email) != 0){
            throw new SignUpException(SignUpErrorCode.EMAIL_IS_DUPLICATED);
        }
    }

    private MailTemplate getMailTemplate(String templateName) {
        return mailTemplateRepository.findByMailTemplateName(templateName)
                .orElseThrow(() -> new MailSenderException(MailErrorCode.WRONG_MAIL_ATTRIBUTE));
    }
    private String getRandomUuid() {
        return UUID.randomUUID().toString();
    }


    /** 이메일 인증 확인*/
    public void checkEmailVerify(String uuid){
        ValueOperations<String, String> value = redisTemplate.opsForValue();

        String email = getEmailFromRedis(uuid, value);
        value.set(SIGNUP_VERIFICATION + email, "true", Duration.ofMinutes(10));
    }

    private String getEmailFromRedis(String uuid, ValueOperations<String, String> value) {
        return Optional.ofNullable(value.get(SIGNUP_UUID + uuid))
                .orElseThrow(() -> new SignUpException(SignUpErrorCode.EXPIRED_EMAIL_OR_WRONG_UUID));
    }

    /** 닉네임 인증*/
    public void checkUsedNickname(String nickName) {
        if(userRepository.countByNickname(nickName) != 0){
            throw new SignUpException(SignUpErrorCode.NICKNAME_IS_DUPLICATED);
        }
    }

    /** 포트폴리오룸 url 인증 */
    public void checkUsedPersonalURL(String personalUrl) {
        if(userRepository.countByPortfolioUrlOnlyArtist(personalUrl) != 0){
            throw new SignUpException(SignUpErrorCode.PERSONAL_URL_IS_DUPLICATED);
        }
    }
}
