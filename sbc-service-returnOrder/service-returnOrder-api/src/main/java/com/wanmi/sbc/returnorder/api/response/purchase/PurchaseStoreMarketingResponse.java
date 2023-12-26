package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.returnorder.bean.vo.PurchaseMarketingViewCalcVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * author:yang
 * Date: 2021-01-13
 */
@Data
@ApiModel
public class PurchaseStoreMarketingResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺营销信息，storeId作为map的key
     */
    @ApiModelProperty(value = "店铺营销信息,key为店铺id，value为营销信息列表")
    private Map<Long, List<PurchaseMarketingViewCalcVO>> storeMarketingMap;

    /**
     * 采购单商品总金额
     */
    @ApiModelProperty(value = "采购单商品总金额")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 采购单商品总金额减去优惠后的金额
     */
    @ApiModelProperty(value = "采购单商品总金额减去优惠后的金额")
    private BigDecimal tradePrice = BigDecimal.ZERO;

    /**
     * 采购单优惠金额
     */
    @ApiModelProperty(value = "采购单优惠金额")
    private BigDecimal discountPrice = BigDecimal.ZERO;

    /**
     * 采购单商品总分销佣金
     */
    @ApiModelProperty(value = "采购单商品总分销佣金")
    private BigDecimal distributeCommission = BigDecimal.ZERO;


    @ApiModelProperty(value = "赠品营销信息")
    private List<PurchaseMarketingViewCalcVO> giftList;

}
