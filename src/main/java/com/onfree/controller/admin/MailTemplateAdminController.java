package com.onfree.controller.admin;

import com.onfree.core.dto.mailtemplate.CreateMailTemplate;
import com.onfree.core.service.MailTemplateService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
@Api(hidden = true)
public class MailTemplateAdminController {
    private final MailTemplateService mailTemplateService;
    //TODO 임시 보류
}
