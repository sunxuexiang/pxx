package com.wanmi.sbc.returnorder.api.request.purchase;

import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-07
 */
@Data
@ApiModel
public class PurchaseCalcMarketingRequest implements Serializable {

    private static final long serialVersionUID = 7221222108601616146L;

    @ApiModelProperty(value = "营销信息")
    private Long marketingId;

    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    @ApiModelProperty(value = "采购单信息")
    private PurchaseFrontRequest frontRequest;

    @ApiModelProperty(value = "客户信息")
    private CustomerDTO customer;

    @ApiModelProperty(value = "商品信息ids")
    private List<String> goodsInfoIds;

    @ApiModelProperty(value = "是否是采购单")
    @NotNull
    private Boolean isPurchase;

    @ApiModelProperty(value = "分仓Id")
    private Long wareId;
}
