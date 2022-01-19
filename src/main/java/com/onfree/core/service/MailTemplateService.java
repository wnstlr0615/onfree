package com.onfree.core.service;

import com.onfree.core.dto.mailtemplate.CreateMailTemplate;
import com.onfree.core.entity.MailTemplate;
import com.onfree.core.repository.MailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static com.onfree.common.constant.MailConstant.CHECK_EMAIL_TEMPLATE;
import static com.onfree.common.constant.MailConstant.PASSWORD_RESET_TEMPLATE;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MailTemplateService {
    private final MailTemplateRepository mailTemplateRepository;

    @PostConstruct
    public void init(){
        saveMailTemplate(CHECK_EMAIL_TEMPLATE, "[이메일 인증] 온프리 이메일 인증 확인", "<a href='<URL>'>이메일 인증하기</a>");
        saveMailTemplate(PASSWORD_RESET_TEMPLATE, "[이메일 인증] 온프리 비밀번호 설정", "<a href='<URL>'>비밀번호 변경하기</a>");
    }

    private void saveMailTemplate(String templateName, String title, String content) {
        mailTemplateRepository.save(
                MailTemplate.builder()
                        .mailTemplateName(templateName)
                        .title(title)
                        .content(content)
                        .build()
        );
    }

    @Transactional
    public CreateMailTemplate.Response saveMailTemplate(CreateMailTemplate.Request request){
        return CreateMailTemplate.Response.fromEntity(
                mailTemplateRepository.save(
                        request.toEntity()
                )
        );
    }




}
