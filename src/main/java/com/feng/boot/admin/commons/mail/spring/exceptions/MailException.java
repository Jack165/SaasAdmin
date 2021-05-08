package com.feng.boot.admin.commons.mail.spring.exceptions;

/**
 * 异常
 *
 * @author bing_huang
 * @since 2.0.0
 */
public class MailException extends RuntimeException {
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
