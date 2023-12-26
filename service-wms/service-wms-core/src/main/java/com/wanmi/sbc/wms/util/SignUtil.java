package com.wanmi.sbc.wms.util;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import sun.misc.BASE64Encoder;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * @author baijianzhong
 * @ClassName SignUtil
 * @Date 2020-07-07 11:08
 * @Description TODO
 **/
public class SignUtil {

    public static String getSignForFLUX(String requestParam){
        try{
            String newsign = FluxConfig.appSecret + requestParam + FluxConfig.appSecret;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(newsign.getBytes("UTF-8"));
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String newsign2 = new BigInteger(1, md.digest()).toString(16);
            //以0开头,BigInteger会把0省略掉，需补全至32位
            int j = 32-newsign2.length();
            for (int i = 0; i < j; i++) {
                newsign2 = "0"+newsign2;
            }
            BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            //base64编码
            encoder.encode(newsign2.getBytes());
            return URLEncoder.encode(newsign2.toUpperCase(), "utf-8");
        }catch (Exception e){
            throw new SbcRuntimeException();
        }
    }
}
