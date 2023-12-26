package com.wanmi.sbc.job.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alipay.api.domain.AreaCode;
import com.alipay.api.domain.Category;
import com.wanmi.sbc.common.constant.Area;
import com.wanmi.sbc.stockout.CityCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 随机生成地址
 * @author lm
 * @date 2022/09/23 16:08
 */
@Slf4j
public class RandomAddressUtil {


    /*市*/
    private static List<CityCode> cityCodeList = new ArrayList<>();

    /*区*/
    private static List<Area> areaList = new ArrayList<>();


    private static String readJson(String url) {
        String address = null;
        ClassPathResource classPathResource = new ClassPathResource(url);
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
            address = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return address;
    }

    /**
     * 获取当前省内所有市数据
     *
     * @param provinceCode
     * @return
     */
    public static List<CityCode> getCityData(String provinceCode) {
        if (cityCodeList == null || cityCodeList.isEmpty()) {
            String address = readJson("cities.json");
            cityCodeList = JSONArray.parseArray(address, CityCode.class);
        }
        return cityCodeList.stream().filter(item -> item.getParentCode().equals(provinceCode)).collect(Collectors.toList());
    }

    /**
     * 根据市代码获取区/县数据
     *
     * @param cityCode
     * @return
     */
    public static List<Area> getAreaData(String cityCode) {
        if (areaList == null || areaList.isEmpty()) {
            String address = readJson("areas.json");
            areaList = JSONArray.parseArray(address, Area.class);
        }
        return areaList.stream().filter(item -> item.getParent_code().equals(cityCode)).collect(Collectors.toList());
    }


    public static JSONArray cateList = new JSONArray();

    /**
     * 获取品类数据
     * @return
     */
    public static JSONArray getCateList() {
        if (cateList == null || cateList.isEmpty()) {
            String address = readJson("cates.json");
            cateList = JSONArray.parseArray(address);
        }
        return cateList;
    }
}