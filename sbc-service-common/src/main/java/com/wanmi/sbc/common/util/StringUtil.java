package com.wanmi.sbc.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 字符串工具类
 * Created by pingchen on 15/12/29.
 */
public final class StringUtil {

    public static String SQL_LIKE_CHAR = "%";

    public static Character SQL_LIKE_ESCAPECHAR = '\\';

    /**
     * 对象转换类型
     * @param object 值
     * @param clazz 对象
     * @return 返回值，否则为null
     */
    public static <T> T cast(Object object, Class<T> clazz){
        if(object != null && clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        return null;
    }

    /**
     * 数组转换类型
     * @param objects 对象数组
     * @param index 索引
     * @param clazz 类型
     * @return 返回值，否则为null
     */
    public static <T> T cast(Object[] objects, int index , Class<T> clazz){
        return cast(objects[index], clazz);
    }

    public static String trunc(String str, int len, String afterPrefix) {
        if (StringUtils.isNotBlank(str) && str.length() > len) {
            return str.substring(0, len).concat(afterPrefix);
        }
        return str;
    }

    public static List<String> pickChs(String str) {
        List<String> strList = new ArrayList<>();
        //使用正则表达式
        Pattern pattern = Pattern.compile("([\u4E00-\u9FA5]{2,})");
        //[\u4E00-\u9FA5]是unicode2的中文区间
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            strList.add(matcher.group());
        }
        return strList;
    }

    public static List<String> pickEng(String str) {
        List<String> strList = new ArrayList<>();
        //使用正则表达式
        Pattern pattern = Pattern.compile("([A-Za-z]{2,})");
        //[\u4E00-\u9FA5]是unicode2的中文区间
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            strList.add(matcher.group());
        }
        return strList;
    }
    public static  String filterString(String  str)  throws PatternSyntaxException {
        // 清除掉所有特殊字符
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        //将所有的特殊字符替换为空字符串
        return m.replaceAll("").trim();
    }
}
