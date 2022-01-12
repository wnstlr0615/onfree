package com.onfree.core.dto.mailtemplate;

import com.onfree.core.entity.MailTemplate;
import lombok.Builder;
import lombok.Getter;

public class CreateMailTemplate {
    @Getter
    @Builder
    public static class Request{
        private final String mailTemplateName;

        private final String title;

        private final String content;

        public MailTemplate toEntity(){
            return MailTemplate.builder()
                    .mailTemplateName(mailTemplateName)
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Response{
        private final Long mailTemplateId;

        private final String mailTemplateName;

        private final String title;

        private final String content;

        public static Response fromEntity(MailTemplate mailTemplate){
            return Response.builder()
                    .mailTemplateId(mailTemplate.getMailTemplateId())
                    .mailTemplateName(mailTemplate.getMailTemplateName())
                    .title(mailTemplate.getTitle())
                    .content(mailTemplate.getContent())
                    .build();
        }
    }
}
