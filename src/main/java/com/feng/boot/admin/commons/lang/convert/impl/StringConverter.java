package com.feng.boot.admin.commons.lang.convert.impl;

import com.feng.boot.admin.commons.lang.convert.AbstractConverter;

/**
 * 字符串转换器
 *
 * @author bing_huang
 * @since 1.0.2
 */
public class StringConverter extends AbstractConverter<String> {
    @Override
    protected String convertInternal(Object value) {
        return super.toStr(value);
    }
}
