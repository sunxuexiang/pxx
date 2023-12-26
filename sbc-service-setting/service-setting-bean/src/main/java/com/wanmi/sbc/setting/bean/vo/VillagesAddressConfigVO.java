package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: 乡镇件地址配置信息VO实体类
 * @author: XinJiang
 * @time: 2022/4/29 9:56
 */
@ApiModel
@Data
public class VillagesAddressConfigVO implements Serializable {

    private static final long serialVersionUID = -724815244800551954L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    private Long villageId;

    /**
     * 省名
     */
    @ApiModelProperty(value = "省名")
    private String provinceName;

    /**
     * 市名
     */
    @ApiModelProperty(value = "市名")
    private String cityName;

    /**
     * 区名
     */
    @ApiModelProperty(value = "区名")
    private String areaName;

    /**
     * 街道名
     */
    @ApiModelProperty(value = "街道名")
    private String villageName;

    /**
     * 省市区中文地址
     */
    @ApiModelProperty(value = "省市区中文地址")
    private String detailAddress;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建日期")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;
}
