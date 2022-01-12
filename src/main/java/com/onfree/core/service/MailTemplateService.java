package com.onfree.core.service;

import com.onfree.core.dto.mailtemplate.CreateMailTemplate;
import com.onfree.core.entity.MailTemplate;
import com.onfree.core.repository.MailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MailTemplateService {
    private final MailTemplateRepository mailTemplateRepository;

    @PostConstruct
    public void init(){
        mailTemplateRepository.save(
                MailTemplate.builder()
                        .mailTemplateName("CHECK_EMAIL")
                        .title("[이메일 인증] 온프리 이메일 인증 확인")
                        .content("<a href='<URL>'>이메일 인증하기</a>")
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
