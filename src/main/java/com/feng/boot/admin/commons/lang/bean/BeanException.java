package com.feng.boot.admin.commons.lang.bean;

import com.feng.boot.admin.commons.lang.exceptions.CommonsLangException;

/**
 * bean异常
 *
 * @author bing_huang
 * @since 1.0.2
 */
public class BeanException extends CommonsLangException {
    public BeanException(Throwable cause) {
        super(cause);
    }
}
