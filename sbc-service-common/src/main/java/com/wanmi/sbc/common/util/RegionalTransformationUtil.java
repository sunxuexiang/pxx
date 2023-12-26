package com.wanmi.sbc.common.util;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.constant.Area;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 区域转换(省、市、区)
 *
 * @author yitang
 * @version 1.0
 */
@Slf4j
public class RegionalTransformationUtil {
    /**
     * 获取省单位
     * @param provinceId
     * @return
     */
    public static Area findProvinceById(String provinceId)  {
        try {
            log.info("RegionalTransformationUtil.findProvinceById province:{}",provinceId);
            InputStream inputStreamProvince = new ClassPathResource("static/provinces.json").getInputStream();
            if (inputStreamProvince != null) {
                String provinceJson = IOUtils.toString(inputStreamProvince, Charset.defaultCharset());
                List<Area> provinces = JSON.parseArray(provinceJson, Area.class);
                Optional<Area> targetProvince = provinces.stream().filter(province -> Objects.equals(province.getCode(),
                                                                                                     provinceId)).findFirst();
                if (targetProvince.isPresent()) {
                    return targetProvince.get();
                }
            }
        } catch (IOException e) {
            return new Area();
        }
        log.info("RegionalTransformationUtil.findProvinceById province is null");
        return new Area();
    }

    /**
     * 获取市单位名称
     * @param cityId
     * @return
     */
    public static Area findCityById(String cityId)  {
        try {
            log.info("RegionalTransformationUtil.findCityById cityId:{}",cityId);
            InputStream inputStreamProvince = new ClassPathResource("static/cities.json").getInputStream();
            if (inputStreamProvince != null) {
                String cityJson = IOUtils.toString(inputStreamProvince, Charset.defaultCharset());
                List<Area> citys = JSON.parseArray(cityJson, Area.class);
                Optional<Area> targetCity = citys.stream().filter(city -> Objects.equals(city.getCode(), cityId)).findFirst();
                if (targetCity.isPresent()) {
                    return targetCity.get();
                }
            }
        } catch (IOException e) {
            return new Area();
        }
        log.info("RegionalTransformationUtil.findCityById city is null");
        return new Area();
    }

    /**
     * 获取区单位名称
     * @param areaId
     * @return
     */
    public static Area findAreaById(String areaId)  {
        try {
            log.info("RegionalTransformationUtil.findAreaById areaId:{}",areaId);
            InputStream inputStreamProvince = new ClassPathResource("static/areas.json").getInputStream();
            if (inputStreamProvince != null) {
                String areaJson = IOUtils.toString(inputStreamProvince, Charset.defaultCharset());
                List<Area> areas = JSON.parseArray(areaJson, Area.class);
                Optional<Area> targetArea = areas.stream().filter(area -> Objects.equals(area.getCode(),
                                                                                         areaId)).findFirst();
                if (targetArea.isPresent()) {
                    return targetArea.get();
                }
            }
        } catch (IOException e) {
            return  new Area();
        }
        log.info("RegionalTransformationUtil.findAreaById area is null");
        return new Area();
    }
}
