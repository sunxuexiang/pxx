package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author jeffrey
 * @create 2021-08-07 9:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBySalesRankingVO implements Serializable {
    private static final long serialVersionUID = 7660392534452852015L;

    @ApiModelProperty("商品SKUID")
    private String goodsInfoId;

    @ApiModelProperty(value = "商品编号SPU")
    private String goodsId;

    @ApiModelProperty(value = "商品标题")
    private String goodsName;

    @ApiModelProperty(value = "商品图片")
    private String goodsImg;

    @ApiModelProperty(value = "商品规格")
    private String goodsSubtitle;

    @ApiModelProperty(value = "商品销量")
    private Long goodsSalesNum;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal marketPrice;;

    @ApiModelProperty(value = "商品分类ID")
    private Long cateId;

    @ApiModelProperty(value = "近一个月商品销量")
    private Long goodsSalesCountOfOneMonth;

    @ApiModelProperty(value = "当前用户购物车中该商品sku数量")
    private Long purchaseNum;

    @ApiModelProperty(value = "sku库存")
    private BigDecimal stock;

    @ApiModelProperty(value = "商品状态")
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    @ApiModelProperty(value = "商品销量提示")
    private String goodsSalesNumTips;

    /**
     * 询问底价标志 0-否，1-是
     * add by jiangxin 20210831
     */
    @ApiModelProperty(value = "询问底价标志", notes = "0-否，1-是")
    private Integer inquiryFlag;

    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    private BigDecimal vipPrice;

    /**
     * 囤货虚拟库存
     */
    @ApiModelProperty(value = "虚拟库存")
    private Long virtualStock;

    /**
     * 是否参与囤货标记
     */
    @ApiModelProperty(value = "是否参与囤货标记")
    private ForcePileFlag pileFlag;


    public void setGoodsSalesNum(Long goodsSalesNum) {
        if (Objects.nonNull(goodsSalesNum) && goodsSalesNum < 10000L){
            this.goodsSalesNumTips = "近7天的销量"+goodsSalesNum+"箱";
        }else {
           this.goodsSalesNumTips = "近7天的销量"+new BigDecimal(goodsSalesNum).divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP)
                   .setScale(0,BigDecimal.ROUND_DOWN)+"万+箱";
        }
        this.goodsSalesCountOfOneMonth = goodsSalesNum;
        this.goodsSalesNum = goodsSalesNum;
    }
}
