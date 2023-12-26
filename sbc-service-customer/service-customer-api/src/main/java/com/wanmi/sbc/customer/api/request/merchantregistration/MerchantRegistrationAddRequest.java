package com.wanmi.sbc.customer.api.request.merchantregistration;

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
 * 商家入驻申请新增信息参数
 * @author hudong
 * @date 2023-06-17 09:03
 */
@ApiModel
@Data
public class MerchantRegistrationAddRequest implements Serializable {


    private static final long serialVersionUID = -2426273139099738867L;
    /**
     * 申请ID
     */
    @ApiModelProperty(value = "申请ID")
    private Long applicationId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String merchantName;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 商家联系方式
     */
    @ApiModelProperty(value = "商家联系方式")
    private String merchantPhone;

    /**
     * 对接人
     */
    @ApiModelProperty(value = "对接人")
    private String contactPerson;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
    /**
     * 是否处理
     */
    @ApiModelProperty(value = "是否处理 0 未处理 1 已处理")
    private Integer handleFlag;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 商家地址
     */
    @ApiModelProperty(value = "商家地址")
    private String merchantAddress;

    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    /**
     * 城市名称
     */
    @ApiModelProperty(value = "城市名称")
    private String cityName;

    /**
     * 地区名称
     */
    @ApiModelProperty(value = "地区名称")
    private String areaName;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String deliveryAddress;

}
