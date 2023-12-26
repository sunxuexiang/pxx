package com.wanmi.sbc.marketing.api.response.market;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingGroupCard;
import com.wanmi.sbc.marketing.bean.vo.PriceInfoOfWholesale;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 促销分组响应结果
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingGroupCardResponse implements Serializable {

    private static final long serialVersionUID = -2023683352977805713L;

    @ApiModelProperty(value = "未参与营销活动的商品列表")
    public List<DevanningGoodsInfoVO> noHaveGoodsInfoVOList;

    @ApiModelProperty(value = "营销活动的商品列表")
    public List<MarketingGroupCard> marketingGroupCards;

    @ApiModelProperty(value = "批发购物车营销活动价格信息(未算散批)")
    public PriceInfoOfWholesale priceInfoOfWholesale;
    private Long storeId;

    //TODO 赠品列表

}
