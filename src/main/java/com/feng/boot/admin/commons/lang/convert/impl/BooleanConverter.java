package com.feng.boot.admin.commons.lang.convert.impl;

import com.feng.boot.admin.commons.lang.BooleanUtils;
import com.feng.boot.admin.commons.lang.convert.AbstractConverter;

/**
 * 布尔转换器
 *
 * @author bing_huang
 * @since 1.0.2
 */
public class BooleanConverter extends AbstractConverter<Boolean> {
    @Override
    protected Boolean convertInternal(Object value) {
        return BooleanUtils.toBoolean(toStr(value));
    }
}
