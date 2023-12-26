package com.wanmi.sbc.shopcart.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货人信息
 * Created by jinwei on 19/03/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ConsigneeDTO implements Serializable {

    private static final long serialVersionUID = -8933071068407917705L;

    /**
     * CustomerDeliveredAddress Id
     */
    @ApiModelProperty(value = "CustomerDeliveredAddress Id")
    private String id;

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
    private Long twonId;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /***
     * 详细地址(包含省市区）
     */
    @ApiModelProperty(value = "详细地址(包含省市区）")
    private String detailAddress;

    @ApiModelProperty(value = "是否乡镇件")
    private Boolean villageFlag=false;

    /**
     * 收货人名称
     */
    @ApiModelProperty(value = "收货人名称")
    private String name;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "收货人电话")
    private String phone;

    /**
     * 期望收货时间
     */
    @ApiModelProperty(value = "期望收货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime expectTime;

   /* *//**
     * 收货地址修改时间
     *//*
    @ApiModelProperty(value = "收货地址修改时间")
    private String updateTime;*/
}
