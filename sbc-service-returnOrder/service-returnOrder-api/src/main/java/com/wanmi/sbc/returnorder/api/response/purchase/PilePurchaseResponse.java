package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 我的囤货
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class PilePurchaseResponse implements Serializable {
    private static final long serialVersionUID = 5739317867937256577L;

    /**
     * 返回
     */
    @ApiModelProperty(value = "我的囤货商品")
    private MicroServicePage<GoodsInfoVO> goodsInfos;

    /**
     * 采购单商品总金额
     */
    @ApiModelProperty(value = "采购单商品总金额")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 我的囤货商品总数量
     */
    @ApiModelProperty(value = "商品总数量")
    private Long goodsTotalCount;
}
