package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCardGroupRequest implements Serializable {

    private static final long serialVersionUID = -494737949098158484L;

    @ApiModelProperty(value = "购物车待计算促销分组优惠的商品列表")
    public  List<DevanningGoodsInfoVO> devanningGoodsInfoVOList;

    /**
     * 会员信息
     */
    @ApiModelProperty(value = "客户信息")
    private String customerId;

    /**
     * 会员信息
     */
    @ApiModelProperty(value = "客户信息")
    private CustomerVO customerVO;

    /**
     * 囤货购物车标识
     */
    @ApiModelProperty(value = "囤货购物车标识")
    private BoolFlag isPileShopcart;
}
