package com.onfree.core.repository;

import com.onfree.core.entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long> {
     Optional<MailTemplate> findByMailTemplateName(String templateName);
}
