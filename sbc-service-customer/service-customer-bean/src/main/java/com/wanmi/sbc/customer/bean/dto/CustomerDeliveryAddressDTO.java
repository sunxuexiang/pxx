package com.wanmi.sbc.customer.bean.dto;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员收货地址-共用查询DTO
 */
@ApiModel
@Data
public class CustomerDeliveryAddressDTO implements Serializable {


    private static final long serialVersionUID = 1314012542242540721L;
    /**
     * 收货地址ID
     */
    @ApiModelProperty(value = "收货地址ID")
    private String deliveryAddressId;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 收货人
     */
    @ApiModelProperty(value = "收货人")
    @NotBlank
    private String consigneeName;

    /**
     * 收货人手机号码
     */
    @ApiModelProperty(value = "收货人手机号码")
    @NotBlank
    private String consigneeNumber;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @NotNull
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
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @NotBlank
    private String deliveryAddress;

    /**
     * 省
     */
    @ApiModelProperty(value = "省名")
    private String provinceName;

    /**
     * 市
     */
    @ApiModelProperty(value = "市名")
    private String cityName;

    /**
     * 区
     */
    @ApiModelProperty(value = "区名")
    private String areaName;

    /**
     * 是否默认地址
     */
    @ApiModelProperty(value = "是否默认地址")
    private DefaultFlag isDefaltAddress = DefaultFlag.NO;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private Double lat;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private Double lng;

    /**
     * 详细地址[最小单位]
     */
    @ApiModelProperty(value = "详细地址[最小单位]")
    private String detailDeliveryAddress;

    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    private Long twonId;

    /**
     * 街道名
     */
    @ApiModelProperty(value = "街道名")
    private String twonName;
}
