package com.wanmi.sbc.customer.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.springframework.util.StringUtils;

/**
 * @Description 拼音工具类
 * @Created by wangning on 2019/8/22 18:49
 */
public class PinyinUtil {

    /**
     * 获取短语的拼音全拼
     * <p>
     * 中文 -> 拼音  其它保持不变
     *
     * @param phrase
     * @return
     */
    public static String getFullPinyinString(String phrase) {
        if (StringUtils.isEmpty(phrase)) {
            return phrase;
        }

        char[] phraseChars = phrase.trim().toCharArray();
        String result = "";

        // 拼音输出
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 不带声调
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 特殊字符转化 绿->lv
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        for (int i = 0; i < phraseChars.length; i++) {
            try {
                // 如果字符是中文,则将中文转为汉语拼音
                if (String.valueOf(phraseChars[i]).matches("[\u4e00-\u9fa5]+")) {
                    result += PinyinHelper.toHanyuPinyinStringArray(phraseChars[i], format)[0] ;
                } else {
                    result += phraseChars[i];
                }
            } catch (Exception e) {
                System.out.println(phraseChars[i] + "字符转化拼音失败");
                e.printStackTrace();
            }
        }
        return result.trim();

    }

    /**
     * 取短语的拼音
     * 中文转拼音 中间用拼音隔开 其它保持不变
     *
     * @param phrase
     * @return
     */
    public static String getFirstLettersLow(String phrase) {
        return getFirstLetters(phrase, HanyuPinyinCaseType.LOWERCASE);
    }


    /**
     * 取短语拼音首字
     *  中文字符取拼音首字  其它不变  字符中间用空格隔开
     *
     * @param phrase
     * @param caseType
     * @return
     */
    private static String getFirstLetters(String phrase, HanyuPinyinCaseType caseType) {
        if (StringUtils.isEmpty(phrase)) {
            return phrase;
        }
        char[] phraseChars = phrase.trim().toCharArray();
        String result = "";

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(caseType);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char item : phraseChars) {
            if(String.valueOf(item).matches("[\u4e00-\u9fa5]+")){
                result +=  getPinyinFirstString(item,format);
            }else{
                result += item;
            }

        }

        return result.trim();
    }


    /**
     * 取中文字符的首字母
     *
     * @param item
     * @param format
     * @return
     */
    private static String getPinyinFirstString(char item, HanyuPinyinOutputFormat format) {
        try {
            String str = String.valueOf(item);
            if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                return PinyinHelper.toHanyuPinyinStringArray(item, format)[0].substring(0, 1);
            }
            return str;
        } catch (Exception e) {
            System.out.println("字符转化拼音失败");
            return "";
        }
    }



    public static void main(String[] args) {
        System.out.println(PinyinUtil.getFullPinyinString("坲冚悳"));
        System.out.println(PinyinUtil.getFullPinyinString("松栢 治咳川贝枇杷滴丸 30mg*60丸/瓶"));
        System.out.println(PinyinUtil.getFullPinyinString("曩憡"));
        System.out.println(PinyinUtil.getFullPinyinString("嘅烀壤"));
        System.exit(0);
    }

}
