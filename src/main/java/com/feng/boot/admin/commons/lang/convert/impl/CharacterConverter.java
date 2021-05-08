package com.feng.boot.admin.commons.lang.convert.impl;

import com.feng.boot.admin.commons.lang.BooleanUtils;
import com.feng.boot.admin.commons.lang.StringUtils;
import com.feng.boot.admin.commons.lang.convert.AbstractConverter;

/**
 * 字符转换器
 *
 * @author bing_huang
 * @since 1.0.2
 */
public class CharacterConverter extends AbstractConverter<Character> {
    @Override
    protected Character convertInternal(Object value) {
        if (value instanceof Boolean) {
            return BooleanUtils.toCharacter((Boolean) value);
        } else {
            final String valueStr = toStr(value);
            if (StringUtils.isNotBlank(valueStr)) {
                return valueStr.charAt(0);
            }
        }
        return null;
    }
}
