package com.wanmi.sbc.shopcart.api.response.purchase;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * author: yang
 * Date: 2020-12-28
 */
@Data
@ApiModel
public class PurchaseMarketingResponse implements Serializable {

    private static final long serialVersionUID = 2323226296834614295L;

    @ApiModelProperty(value = "单品营销信息map", notes = "key为单品id，value为营销列表")
    private Map<String, List<MarketingViewVO>> map;

    @ApiModelProperty(value = "单品信息列表")
    private List<GoodsInfoVO> goodsInfos;

    /**
     * 商品选择的营销
     */
    @ApiModelProperty(value = "商品选择的营销")
    private List<GoodsMarketingVO> goodsMarketings;

    /**
     * 店铺营销信息，storeId作为map的key
     */
    @ApiModelProperty(value = "店铺营销信息,key为店铺id，value为营销信息列表")
    private Map<Long, List<PurchaseMarketingCalcVO>> storeMarketingMap;
}
