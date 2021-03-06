package com.feng.boot.admin.commons.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.feng.boot.admin.commons.json.utils.Jsons;
import com.feng.boot.admin.commons.lang.Validate;

import java.util.List;
import java.util.Map;

/**
 * alibaba fast json Utils
 *
 * @author bing_huang
 * @see FastJsonImpl
 * @see Jsons
 * @since 1.0.1
 * @deprecated 2.0.0
 */
public class FastJsonUtils {

    /**
     * json 字符串转对象
     *
     * @param json json字符串，不为空
     * @param type 需要转换的类型，不为空
     * @param <T>  对象转换类型
     * @return 对象指定类型
     */
    public static <T> T jsonToObject(String json, Class<T> type) {
        return jsonToObject(json, type, ParserConfig.getGlobalInstance());
    }

    /**
     * json 字符串转对象
     *
     * @param json   json字符串，不为空
     * @param type   需要转换的类型，不为空
     * @param config {@link ParserConfig}，不为空
     * @param <T>    对象转换类型
     * @return 对象指定类型
     */
    public static <T> T jsonToObject(String json, Class<T> type, ParserConfig config) {
        Validate.notBlank(json, "json content must be not null");
        Validate.notNull(type, "target class type must be not null");
        Validate.notNull(config, "parser config must be not null");
        return JSON.parseObject(json, type, config);
    }

    /**
     * json字符串转list
     *
     * @param json json字符串不为空
     * @param type 转换的类型,不为空
     * @param <T>  对象转换类型
     * @return 对象指定类型
     */
    public static <T> List<T> jsonToList(String json, Class<T> type) {
        return jsonToList(json, type, ParserConfig.getGlobalInstance());
    }

    /**
     * json 字符串转list对象
     *
     * @param json   json字符串，不为空
     * @param type   需要转换的类型，不为空
     * @param config {@link ParserConfig}，不为空
     * @param <T>    对象转换类型
     * @return 对象指定类型
     */
    public static <T> List<T> jsonToList(String json, Class<T> type, ParserConfig config) {
        Validate.notBlank(json, "json content must be not null");
        Validate.notNull(type, "target class type must be not null");
        Validate.notNull(config, "parser config must be not null");
        return JSONArray.parseArray(json, type, config);
    }


    /**
     * 将对象转换成string json
     *
     * @param source 目标对象，不为空
     * @return json string
     */
    public static String objectToJson(Object source) {
        return objectToJson(source, SerializeConfig.getGlobalInstance());
    }

    /**
     * 将对象转换成string json
     *
     * @param source 目标对象，不为空
     * @param config {@link SerializeConfig},不为空
     * @return json string
     */
    public static String objectToJson(Object source, SerializeConfig config) {
        Validate.notNull(source, "source object must be not null");
        Validate.notNull(config, "serialize config must be not null");
        return JSON.toJSONString(source, config);
    }

    /**
     * 将map类型转成对象类型
     *
     * @param sourceMap 源map类型对象，不为空
     * @param type      需要转成的对象类型，不为空
     * @param <T>       目标对象类型
     * @return 对象指定类型
     */
    public static <T> T mapToObject(Map<String, ?> sourceMap, Class<T> type) {
        return mapToObject(sourceMap, type, SerializeConfig.getGlobalInstance());
    }

    /**
     * 将map类型转成对象类型
     *
     * @param sourceMap 源map类型对象，不为空
     * @param type      需要转成的对象类型，不为空
     * @param config    {@link SerializeConfig}，不为空
     * @param <T>       目标对象类型
     * @return 对象指定类型
     */
    public static <T> T mapToObject(Map<String, ?> sourceMap, Class<T> type, SerializeConfig config) {
        Validate.notNull(sourceMap, "source map must be not null");
        String json = objectToJson(sourceMap, config);
        return jsonToObject(json, type);
    }

    /**
     * 将 源目标对象转成map类型
     *
     * @param source 源目标，不为空
     * @return map实例
     */
    public static Map<?, ?> objectToMap(Object source) {
        return objectToMap(source, SerializeConfig.getGlobalInstance());
    }

    /**
     * 将 源目标对象转成map类型
     *
     * @param source 源目标，不为空
     * @param config {@link SerializeConfig},不为空
     * @return map实例
     */
    public static Map<?, ?> objectToMap(Object source, SerializeConfig config) {
        String json = objectToJson(source, config);
        return jsonToObject(json, Map.class);
    }

}
