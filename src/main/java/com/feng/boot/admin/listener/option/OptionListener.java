package com.feng.boot.admin.listener.option;

import com.feng.boot.admin.event.option.OptionUpdatedEvent;
import com.feng.boot.admin.project.notice.mail.model.dto.MailDTO;
import com.feng.boot.admin.project.notice.mail.service.IMailService;
import com.feng.boot.admin.commons.mail.spring.properties.MailProperties;
import com.feng.boot.admin.commons.mail.spring.service.MailService;
import com.feng.boot.admin.commons.mail.spring.service.impl.MailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Nonnull;

/**
 * option listener
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Component
@RequiredArgsConstructor
public class OptionListener implements ApplicationListener<OptionUpdatedEvent> {
    private MailService mailService;
    private final FreeMarkerConfigurer markerConfigurer;
    private final IMailService mail;

    @Override
    public void onApplicationEvent(@Nonnull OptionUpdatedEvent event) {
        createMail();
    }

    /**
     * 获取mail服务
     *
     * @return {@link MailService}
     */
    public MailService getMailService() {
        if (null == mailService) {
            createMail();
        }
        return this.mailService;
    }

    /**
     * 创建mail服务
     */
    public void createMail() {
        MailProperties properties = new MailProperties();
        MailDTO info = mail.info();
        properties.setHost(info.getHost());
        properties.setProtocol(info.getProtocol());
        properties.setSslPort(info.getSslPort());
        properties.setUsername(info.getUsername());
        properties.setPassword(info.getPassword());
        properties.setEnabled(info.getEnabled() != null && info.getEnabled() == 1);
        properties.setPassword(info.getPassword());
        properties.setFromName(info.getFromName());
        this.mailService = new MailServiceImpl(properties, markerConfigurer);
    }
}
