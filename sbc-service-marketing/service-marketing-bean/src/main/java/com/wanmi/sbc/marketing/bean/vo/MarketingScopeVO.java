package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-19 14:16
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingScopeVO implements Serializable {

    private static final long serialVersionUID = -7950579506250193744L;
    /**
     * 货品与促销规则表Id
     */
    @ApiModelProperty(value = "营销和商品关联表Id")
    private Long marketingScopeId;

    /**
     * 促销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 促销范围Id
     */
    @ApiModelProperty(value = "营销范围Id")
    private String scopeId;


    @ApiModelProperty(value = "是否终止： 0.未终止 1.终止")
    private BoolFlag terminationFlag =BoolFlag.NO;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "单用户限购数量")
    private Long perUserPurchaseNum;


    /**
     * 限购数量
     */
    @ApiModelProperty(value = "总限购数量")
    private Long purchaseNum;
}
