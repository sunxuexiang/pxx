package com.wanmi.sbc.marketing.bean.dto;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * <p>营销和商品关联关系</p>
 * author: sunkun
 * Date: 2018-11-19
 */
@ApiModel
@Data
public class MarketingScopeDTO implements Serializable {

    private static final long serialVersionUID = -3177165143639231339L;

    /**
     * 货品与营销规则表Id
     */
    @ApiModelProperty(value = "货品与营销规则表Id")
    private Long marketingScopeId;

    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 营销范围Id
     */
    @ApiModelProperty(value = "营销范围Id")
    private String scopeId;


    @ApiModelProperty(value = "是否终止： 0.未终止 1.终止")
    private BoolFlag terminationFlag;

    /**
     * 是否为必选商品  0：非必选  1：必选
     */
    @ApiModelProperty(value = "是否为必选商品  0：非必选  1：必选")
    private BoolFlag whetherChoice;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;
}
