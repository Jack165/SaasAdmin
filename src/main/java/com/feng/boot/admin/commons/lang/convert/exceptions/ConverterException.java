package com.feng.boot.admin.commons.lang.convert.exceptions;

import com.feng.boot.admin.commons.lang.exceptions.CommonsLangException;

/**
 * 转换异常
 *
 * @author bing_huang
 * @since 2.0.0
 */
public class ConverterException extends CommonsLangException {
    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
