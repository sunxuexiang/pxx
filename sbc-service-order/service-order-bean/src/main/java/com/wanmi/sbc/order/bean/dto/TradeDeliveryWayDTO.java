package com.wanmi.sbc.order.bean.dto;

import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TradeDeliveryWayDTO
 * @Description TODO
 * @Author shiy
 * @Date 2023/7/5 8:59
 * @Version 1.0
 */
@ApiModel
@Data
public class TradeDeliveryWayDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 店铺标识
     */
    @ApiModelProperty(value = "店铺标识")
    private Long storeId;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private Integer companyType;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;


    /**
     * 店铺商品总数量
     */
    @ApiModelProperty(value = "店铺商品总数量")
    private Long totalSkuNum;
}
