package com.wanmi.sbc.customer.address.request;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 客户配送地址
 * Created by CHENLI on 2017/4/20.
 */
@Data
public class CustomerDeliveryAddressEditRequest {
    /**
     * 收货地址ID
     */
    private String deliveryAddressId;
    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 收货人
     */
    @NotBlank
    private String consigneeName;

    /**
     * 收货人手机号码
     */
    @NotBlank
    private String consigneeNumber;

    /**
     * 省
     */
    @NotNull
    private Long provinceId;

    /**
     * 市
     */
    private Long cityId;

    /**
     * 区
     */
    private Long areaId;

    /**
     * 详细地址
     */
    @NotBlank
    private String deliveryAddress;

    /**
     * 省名
     */
    private String provinceName;

    /**
     * 市名
     */
    private String cityName;

    /**
     * 区名
     */
    private String areaName;

    /**
     * 是否默认地址
     */
    private DefaultFlag isDefaltAddress = DefaultFlag.NO;


    /**
     * 纬度
     */
    private Double lat;

    /**
     * 经度
     */
    private Double lng;

    /**
     * 详细地址[最小单位]
     */
    private String detailDeliveryAddress;

    /**
     * 街道
     */
    private Long twonId;

    /**
     * 街道名
     */
    private String twonName;


    /**
     * 纬度值
     */

    private BigDecimal nLat;


    /**
     * 经度值
     */

    private BigDecimal nLng;
}
