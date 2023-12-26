package com.wanmi.sbc.returnorder.api.response.purchase;

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
import java.time.LocalDateTime;
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

    @ApiModelProperty(value = "店铺id")
    public Long  storeId;

    @ApiModelProperty(value = "店铺名称")
    public String  storeName;

    /**
     * 批发市场Id
     */
    @ApiModelProperty(value = "批发市场Id")
    private Long mallBulkMarketId;

    /**
     * 批发市场名称
     */
    @ApiModelProperty(value = "批发市场名称")
    private String mallBulkMarketName;

    @ApiModelProperty(value = "购物车类型")
    private String key;

    @ApiModelProperty(value = "商家类型")
    public Integer companyType;

    //商家最近一次添加囤货购物车商品的时间
    public LocalDateTime addTime;
}
