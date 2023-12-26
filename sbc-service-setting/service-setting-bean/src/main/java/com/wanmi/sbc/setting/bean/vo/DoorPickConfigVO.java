package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 网点实体VO
 */
@ApiModel
@Data
public class DoorPickConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long networkId;

    /**
     * 网点联系人
     */
    @ApiModelProperty(value = "网点联系人")
    private String contacts;

    /**
     * 网点名字
     */
    @ApiModelProperty(value = "网点名字")
    private String networkName;

    /**
     * 网点手机号码
     */
    @ApiModelProperty(value = "网点手机号码")
    private String phone;


    /**
     * 网点座机号码
     */
    @ApiModelProperty(value = "网点座机号码")
    private String landline;


    /**
     * 网点地址
     */
    @ApiModelProperty(value = "网点地址")
    private String networkAddress;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String province;


    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private String area;

    /**
     * 镇
     */
    @ApiModelProperty(value = "镇")
    private String town;



    @ApiModelProperty(value = "省名称")
    private String provinceName;


    @ApiModelProperty(value = "市名称")
    private String cityName;

    @ApiModelProperty(value = "区名称")
    private String areaName;

    @ApiModelProperty(value = "镇名称")
    private String townName;

    @ApiModelProperty(value = "镇名称")
    private String specificAdress;

    /**
     * 纬度值
     */
    @ApiModelProperty(value = "纬度值")
    private BigDecimal lat;

    /**
     * 经度值
     */
    @ApiModelProperty(value = "经度值")
    private BigDecimal lng;

    /**
     * 可配送距离 米
     */
    @ApiModelProperty(value = "可配送距离 米")
    private int distance;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除")
    private Integer delFlag =0;

    /**
     * 支付锁定时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;


    /**
     * 支付锁定时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deleteTime;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

}
