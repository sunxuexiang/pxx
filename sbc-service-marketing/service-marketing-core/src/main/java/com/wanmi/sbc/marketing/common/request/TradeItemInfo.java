package com.wanmi.sbc.marketing.common.request;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>获取营销优惠时传入的订单商品信息参数封装</p>
 * Created by of628-wenzhi on 2018-03-14-下午6:02.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeItemInfo {
    /**
     * 单品id
     */
    private String skuId;

    /**
     * 商品id
     */
    private String spuId;

    /**
     * 商品价格（计算区间价或会员价后的应付金额）
     */
    private BigDecimal price;

    /**
     * 商品购买数量
     */
    private Long num;

    /**
     * 倍数
     */
    private BigDecimal divisorFlag;


    /**
     * 商品的总价已经为数量乘以市场价
     */

    private BigDecimal allMarketPrice;



    /**
     * 商品的总价已经为数量乘以市场价
     */

    private BigDecimal allVipPrice;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商品品牌id
     */
    private Long brandId;

    /**
     * 分类id
     */
    private Long cateId;

    /**
     * 店铺分类
     */
    private List<Long> storeCateIds;

    /**
     * 分销商品审核状态
     */
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    @ApiModelProperty("商品状态（0：普通商品，1：特价商品）")
    private Integer goodsInfoType;

    /**
     * 销售类型 0批发 1零售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售 2散批")
    private SaleType saleType = SaleType.WHOLESALE;
}
