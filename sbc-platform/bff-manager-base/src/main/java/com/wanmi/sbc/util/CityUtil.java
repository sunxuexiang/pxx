package com.wanmi.sbc.util;

import com.alibaba.fastjson.JSONArray;
import com.wanmi.sbc.stockout.CityCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: CityUtil
 * @Description: TODO
 * @Date: 2020/9/25 20:03
 * @Version: 1.0
 */
public class CityUtil {

    public static  Map<String,CityCode> getCityNameMap(){
       return toCityName("cities.json");
    }

    public static Map<String,CityCode> getProvincesMap(){
       return toCityName("provinces.json");
    }

    private static Map<String,CityCode> toCityName(String url) {
        String city = null;
        ClassPathResource classPathResource = new ClassPathResource(url);
        InputStream inputStream=null;
        try {
            inputStream = classPathResource.getInputStream();
            city = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null!=inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (StringUtils.isNotBlank(city)) {
            List<CityCode> tradeAddDTOS = JSONArray.parseArray(city, CityCode.class);
            return tradeAddDTOS.stream().collect(Collectors.toMap
                    (CityCode::getCode, Function.identity()));
        } else {
            return null;
        }
    }
}
