package com.onfree.core.entity;

import com.onfree.common.model.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MailTemplate extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mailTemplateId;

    @Column(unique = true, nullable = false)
    private String mailTemplateName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

}
