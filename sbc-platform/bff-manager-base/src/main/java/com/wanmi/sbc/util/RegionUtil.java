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
 * @Description: 获取region列表
 */
public class RegionUtil {

    public static  List<CityCode> getCityList(){
       return toCityName("cities.json");
    }

    public static List<CityCode> getProvincesList(){
       return toCityName("provinces.json");
    }

    private static List<CityCode> toCityName(String url) {
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
            return tradeAddDTOS;
        } else {
            return null;
        }
    }
}
