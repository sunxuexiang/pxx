package com.wanmi.sbc.common.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName ObjectUtils
 * @Description TODO
 * @Author shiy
 * @Date 2023/6/20 8:56
 * @Version 1.0
 */
public class ObjectUtils {
    /**
     * @param obj
     * @param other
     * @Description: 比较String是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:56:49
     */
    public static boolean equals(String obj, String other, boolean enableNullEqual) {
        if (StringUtils.isEmpty(obj) && StringUtils.isEmpty(other)) {
            return enableNullEqual;
        }
        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.equals(other);
    }

    public static boolean equals(String obj, String other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.equals(other);
    }

    /**
     * @param obj
     * @param other
     * @Description: 比较Long是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:57:09
     */
    public static boolean equals(Long obj, Long other, boolean enableNullEqual) {
        if (obj == null && other == null) {
            return enableNullEqual;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.longValue() == other.longValue();
    }



    public static boolean equals(Long obj, Long other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.longValue() == other.longValue();
    }

    /**
     * @param obj
     * @param other
     * @Description: 比较BigDecimal是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:57:18
     */
    public static boolean equals(BigDecimal obj, BigDecimal other, boolean enableNullEqual) {
        if (obj == null && other == null) {
            return enableNullEqual;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.compareTo(other) == 0;
    }

    public static boolean equals(BigDecimal obj, BigDecimal other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.compareTo(other) == 0;
    }

    /**
     * @param obj
     * @param other
     * @Description: 比较Date是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:57:27
     */
    public static boolean equals(Date obj, Date other, boolean enableNullEqual) {
        if (obj == null && other == null) {
            return enableNullEqual;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.compareTo(other) == 0;
    }

    public static boolean equals(Date obj, Date other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.compareTo(other) == 0;
    }

    /**
     * @param obj
     * @param other
     * @Description: 比较Integer是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:57:34
     */
    public static boolean equals(Integer obj, Integer other, boolean enableNullEqual) {
        if (obj == null && other == null) {
            return enableNullEqual;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.intValue() == other.intValue();
    }

    public static boolean equals(Integer obj, Integer other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.intValue() == other.intValue();
    }

    /**
     * @param obj
     * @param other
     * @Description: 比较Double是否相等
     * @Param:
     * @return: boolean
     * @Author: 石勇
     * @Date: 2020-09-08 10:57:57
     */
    public static boolean equals(Double obj, Double other, boolean enableNullEqual) {
        if (obj == null && other == null) {
            return enableNullEqual;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.doubleValue() == other.doubleValue();
    }

    public static boolean equals(Double obj, Double other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.doubleValue() == other.doubleValue();
    }

    public static boolean equalsObj(Object obj, Object other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj != null && other == null) {
            return false;
        }
        if (obj == null && other != null) {
            return false;
        }
        return obj.equals(other);
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String,String>();
        List<Field> allField = getAllField(obj);
        for (Field field : allField) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = "";
            if (field.getType()== String.class || field.getType() == Integer.class || field.getType() == int.class){
                fieldValue = field.get(obj)==null?"": field.get(obj).toString();
            }
            map.put(fieldName, fieldValue);
        }
        return map;
    }

    private static List<Field> getAllField(Object obj){
        List<Field> fieldList = new ArrayList<Field>() ;
        Class<?> clazz = obj.getClass();
        while (clazz != null){
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;

    }
}
