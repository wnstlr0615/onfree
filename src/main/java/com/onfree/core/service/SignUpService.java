package com.onfree.core.service;

import com.onfree.common.error.code.MailErrorCode;
import com.onfree.common.error.code.SignUpErrorCode;
import com.onfree.common.error.exception.MailSenderException;
import com.onfree.common.error.exception.SignUpException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.entity.MailTemplate;
import com.onfree.core.repository.MailTemplateRepository;
import com.onfree.core.repository.UserRepository;
import com.onfree.utils.MailComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class SignUpService {
    public static final String SIGNUP_UUID = "signup:uuid:";
    public static final String SIGNUP_VERIFICATION = "signup:verification:";
    private final MailTemplateRepository mailTemplateRepository;
    private final UserRepository userRepository;
    private final MailComponent mailComponent;
    private final StringRedisTemplate redisTemplate;

    /** 이메일 인증 */
    @Async(value = "getAsyncExecutor")
    public void asyncEmailVerify(String email) {
        validDuplicatedEmail(email);
        MailTemplate checkEmailTemplate = getMailTemplate("CHECK_EMAIL");
        UUID uuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(SIGNUP_UUID + uuid, email, Duration.ofSeconds(300));
        String content = getContent(checkEmailTemplate.getContent(), uuid);
        mailComponent.sendMail(email, checkEmailTemplate.getTitle(), content);
    }

    private String getContent( String content, UUID uuid) {
        return content.replace("<URL>", "http://localhost:8080/test/api/signup/" + uuid);
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

    /** 이메일 인증 확인*/
    public SimpleResponse checkEmailVerify(String uuid){
        checkVerificationEmailFromRedis(uuid);
        return SimpleResponse.success("이메일 인증이 완료되었습니다.");
    }

    private void checkVerificationEmailFromRedis(String uuid) {
        ValueOperations<String, String> value = redisTemplate.opsForValue();

        String email = getEmailFromRedis(uuid, value);
        value.set(SIGNUP_VERIFICATION +email, "true", Duration.ofMinutes(10));
    }

    private String getEmailFromRedis(String uuid, ValueOperations<String, String> value) {
        return Optional.ofNullable(value.get("signUp:uuid:" + uuid))
                .orElseThrow(() -> new SignUpException(SignUpErrorCode.EXPIRED_EMAIL_OR_WRONG_UUID));
    }

    /** 닉네임 인증*/
    public SimpleResponse checkUsedNickname(String nickName) {
        validDuplicatedNickname(nickName);
        return SimpleResponse.success("해당 닉네임은 사용가능합니다.");
    }

    private void validDuplicatedNickname(String nickName) {
       if(userRepository.countByNickname(nickName) != 0){
           throw new SignUpException(SignUpErrorCode.NICKNAME_IS_DUPLICATED);
       }
    }

    public SimpleResponse checkUsedPersonalURL(String personalUrl) {
        if(userRepository.countByPortfolioUrlOnlyArtist(personalUrl) != 0){
            throw new SignUpException(SignUpErrorCode.PERSONAL_URL_IS_DUPLICATED);
        }
        return SimpleResponse.success("해당 URL  은 사용 가능 합니다.");
    }
}
