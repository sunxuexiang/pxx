package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * <p>订单确认返回项</p>
 * Created by of628-wenzhi on 2018-03-08-下午6:12.
 */
@Data
@ApiModel
public class TradeConfirmItemVO {
    /**
     * 订单商品sku
     */
    @ApiModelProperty(value = "订单商品sku")
    private List<TradeItemVO> tradeItems;

    /**
     * 赠品列表
     */
    @ApiModelProperty(value = "赠品列表")
    private List<TradeItemVO> gifts;

    /**
     * 商家与店铺信息
     */
    @ApiModelProperty(value = "商家与店铺信息")
    private SupplierVO supplier;

    /**
     * 订单项小计
     */
    @ApiModelProperty(value = "订单项小计")
    private TradePriceVO tradePrice;

    /**
     * 优惠金额
     */
    @ApiModelProperty(value = "优惠金额")
    private List<DiscountsVO> discountsPrice;

    @ApiModelProperty(value = "店铺商品+赠品数量")
    private Long goodsNum;

    /**
     * 销售类型 0批发 1零售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售")
    private SaleType saleType = SaleType.WHOLESALE;

    private String tradeconfirmRmark;

    public String getTradeconfirmRmark() {
        if(CollectionUtils.isNotEmpty(tradeItems)){
            Long goodsTotalNum = tradeItems.stream().map(TradeItemVO::getNum).reduce(Long::sum).orElse(0L);
            tradeconfirmRmark = "共"+goodsTotalNum+"箱";
        }else{
            tradeconfirmRmark = Constants.EMPTY_STR;
        }
        return tradeconfirmRmark;
    }
}
