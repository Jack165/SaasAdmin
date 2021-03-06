package com.feng.boot.admin.commons.lang.net;

import com.feng.boot.admin.commons.lang.StringUtils;
import com.feng.boot.admin.commons.lang.Validate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * url util
 *
 * @author bing_huang
 * @since 1.0.0
 */
public class UrlUtils {
    /**
     * url 编码, 默认utf-8
     *
     * @param params str
     * @return str
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @see UrlUtils#urlEncode(String, String)
     */
    public static String urlEncode(String params) throws UnsupportedEncodingException {
        Validate.notNull(params, "params must be not null");
        return urlEncode(params, StandardCharsets.UTF_8.displayName());
    }

    /**
     * url 编码，
     *
     * @param params url参数，不允许为空，
     * @param enc    编码规则,{@link StandardCharsets}，默认utf8
     * @return 已编码后的url
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @see #urlEncode(String, Charset)
     */
    public static String urlEncode(String params, String enc) throws UnsupportedEncodingException {
        Charset charset = StringUtils.isBlank(enc) ? StandardCharsets.UTF_8 : Charset.forName(enc);
        return urlEncode(params, charset);
    }

    /**
     * url 编码
     *
     * @param params url参数
     * @param enc    编码集,{@link StandardCharsets}
     * @return 已编码后的url
     * @throws UnsupportedEncodingException 编码异常
     * @since 1.0.2
     */
    public static String urlEncode(String params, Charset enc) throws UnsupportedEncodingException {
        Validate.notNull(params, "params must be not null");
        String encode = URLEncoder.encode(params, enc.displayName());
        return encode.replace("+", "%20")
                .replace("*", "%2A")
                .replace("~", "%7E")
                .replace("/", "%2F");
    }


    /**
     * url 解码,默认UTF-8
     *
     * @param params str 需要编码的参数,不为null
     * @return str 已经完成编码的参数
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @see #urlDecoder(String, String)
     */
    public static String urlDecoder(String params) throws UnsupportedEncodingException {
        return urlDecoder(params, StandardCharsets.UTF_8.displayName());
    }

    /**
     * url 解码 默认 utf8
     *
     * @param params str,不为null
     * @param enc    解码规则
     * @return str 已解码字符串
     * @throws UnsupportedEncodingException 解码失败
     * @see #urlDecoder(String, Charset)
     */
    public static String urlDecoder(String params, String enc) throws UnsupportedEncodingException {
        Charset charset = StringUtils.isBlank(enc) ? StandardCharsets.UTF_8 : Charset.forName(enc);
        return urlDecoder(params, charset);
    }

    /**
     * url解码
     *
     * @param params 已编码的url,不为null
     * @param enc    解码规则,{@link StandardCharsets},不为null
     * @return 已完成解码后的url
     * @throws UnsupportedEncodingException 解码失败
     * @since 1.0.2
     */
    public static String urlDecoder(String params, Charset enc) throws UnsupportedEncodingException {
        Validate.notNull(params, "params must be not null");
        Validate.notNull(enc, "charset must be not null");
        return URLDecoder.decode(params, enc.displayName());
    }

}
