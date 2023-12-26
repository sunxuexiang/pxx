package com.wanmi.sbc.order.bean.vo;


import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.TradeMarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

/**
 * 商品SKU实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
@Accessors(chain = true)
public class UnsatisfiedMarketingVO implements Serializable {


    private static final long serialVersionUID = 4767581125602181716L;

    @ApiModelProperty(value = "库存缺少商品")
    private List<GoodsInfoVO> stockOut;

    @ApiModelProperty(value = "失效营销")
    private List<TradeMarketingVO> resultTradeMarketings;

    @ApiModelProperty(value = "失效优惠金额")
    private BigDecimal resultMoney=BigDecimal.ZERO;

    @ApiModelProperty(value = "赠品集合")
    private List<GoodsInfoVO> gift;

    private Boolean changeMarketingFlag;

    @ApiModelProperty(value = "是否继续提交")
    private Boolean commit;
}