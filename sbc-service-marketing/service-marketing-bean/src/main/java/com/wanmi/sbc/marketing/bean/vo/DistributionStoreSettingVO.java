package com.wanmi.sbc.marketing.bean.vo;

import java.math.BigDecimal;

import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>店铺分销设置VO</p>
 *
 * @author gaomuwei
 * @date 2019-02-19 15:46:27
 */
@ApiModel
@Data
public class DistributionStoreSettingVO {

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private String storeId;

    /**
     * 是否开启社交分销 0：关闭，1：开启
     */
    @ApiModelProperty(value = "是否开启社交分销")
    private DefaultFlag openFlag;

    /**
     * 是否开启通用分销佣金 0：关闭，1：开启
     */
    @ApiModelProperty(value = "是否开启通用分销佣金")
    private DefaultFlag commissionFlag;

    /**
     * 通用分销佣金
     */
    @ApiModelProperty(value = "通用分销佣金")
    private BigDecimal commission;

    /**
     * 通用佣金比例
     */
    @ApiModelProperty(value = "通用佣金比例")
    private BigDecimal commissionRate;



}