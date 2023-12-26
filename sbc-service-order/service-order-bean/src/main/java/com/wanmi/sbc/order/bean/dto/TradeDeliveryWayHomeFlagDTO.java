package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName TradeDeliveryWayHomeFlagDTO
 * @Description TODO
 * @Author shiy
 * @Date 2023/7/5 8:59
 * @Version 1.0
 */
@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeDeliveryWayHomeFlagDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否满足送货到家标志位
     */
    @ApiModelProperty(value = "是否满足送货到家标志位，0：不满足，1：满足")
    private DefaultFlag flag;

    @ApiModelProperty(value = "如果是湖北地址且不是免店配就返回最最近战点地址")
    private String adress;
    @ApiModelProperty(value = "地址")
    private String networkAddress;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "网点id")
    private Long networkId;

    @ApiModelProperty(value = "网点电话号码")
    private String phone;

    @ApiModelProperty(value = "网点名称")
    private String networkName;

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

    /**
     * 自提提示
     */
    @ApiModelProperty(value = "自提提示")
    private String pickNote;
}
