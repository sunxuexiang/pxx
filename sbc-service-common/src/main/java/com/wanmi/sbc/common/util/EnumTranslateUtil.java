package com.wanmi.sbc.common.util;

import com.wanmi.sbc.common.annotation.ApiEnumProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取Enum的值
 */
public class EnumTranslateUtil {
    public static Map<String,String> getFieldAnnotation(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String, String> resultMap = new HashMap();
        for (Field field : fields) {
            // 是否引用ApiEnumProperty注解
            boolean bool = field.isAnnotationPresent(ApiEnumProperty.class);
            if (bool) {
                String value = field.getAnnotation(ApiEnumProperty.class).value().substring(2);
                resultMap.put(field.getName(), value);
            }
        }
        return resultMap;
    }

}
